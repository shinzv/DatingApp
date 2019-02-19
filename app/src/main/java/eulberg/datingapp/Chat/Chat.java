package eulberg.datingapp.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import eulberg.datingapp.Message;
import eulberg.datingapp.R;
import eulberg.datingapp.User;

/**
 * Die Klasse Chat wird benutzt um einen Chat zwischen einem selber und einem anderen Chatteilnehmer zu verwalten
 */
public class Chat extends AppCompatActivity {

    //Referenzen
    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ImageButton sendButton;
    EditText message;

    Intent intent;

    private ImageView backButton;
    private ChatAdapter chatAdapter;

    ArrayList<Message> messages;
    RecyclerView messagesRecyclerView;

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Initialisierungen usw...
     * @param savedInstanceState der gespeicherte Status der App
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        messagesRecyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.messageImage);
        username = findViewById(R.id.messageUsername);
        sendButton = findViewById(R.id.sendButton);
        message = findViewById(R.id.messageText);

        intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+userID);
                long megabyte = 1024 * 1024;
                storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Glide.with(Chat.this).asBitmap().load(bytes).into(profile_image);
                    }
                });
                readMessages(firebaseUser.getUid(), userID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(messageIsValid(msg)){
                    sendMessage(firebaseUser.getUid(),userID,removeUnnecessarySpacesAndBreaks(msg));
                }else{
                    Toast.makeText(Chat.this,"Tippe eine Nachricht ein",Toast.LENGTH_SHORT).show();
                }
                message.setText("");
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayProfile = new Intent(Chat.this, DisplayProfile.class);
                displayProfile.putExtra("userID",userID);
                startActivity(displayProfile);
            }
        });
    }

    /**
     * Der Methode wird eine Nachricht übergeben, bei der redundante Leerzeichen sowie Leerzeilen werden entfernt.
     * @param message Die Nachricht welche bearbeitet wird.
     * @return Die überarbeite Nachricht ohne unnötige Leerzechen sowie Leerzeilen wird zurückgegeben.
     */
    private String removeUnnecessarySpacesAndBreaks(String message) {
        while(message.startsWith(" ") || message.startsWith("\n") || message.endsWith(" ") || message.endsWith("\n")){
            if (message.startsWith(" ") || message.startsWith("\n")) {
                message = message.substring(1);
            }else if (message.endsWith(" ") || message.endsWith("\n")){
                message = message.substring(0,message.length()-1);
            }
        }
        return message;
    }


    /**
     * Der Methode wird eine Nachricht übergeben, bei der überprüft wird, ob sie nicht nur aus Leerzeichen und Leerzeilen besteht
     * @param message Die Nachricht welche bearbeitet wird.
     * @return Es wird wahr zurückgegeben, wenn die Nachricht nicht nur aus Leerzeichen und Leerzeilen besteht.
     */
    private boolean messageIsValid(String message) {
        String messageWithoutBreaks = message.replace("\n","");
        String messageWithoutSpaces = messageWithoutBreaks.replace(" ","");
        if(!messageWithoutSpaces.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Die Methode wird aufgerufen um eine übergebene Nachricht mit Informationen über den Sender und den Empfänger in die Datenbank hochzuladen.
     * @param sender Die UserID des Senders.
     * @param receiver Die UserID des Empfängers.
     * @param message Die Nachricht.
     */
    private void sendMessage(String sender, String receiver,String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("chats").push().setValue(hashMap);

    }

    /**
     * Die Methode wird benutzt um die zu einem Chat zugehörigen Nachrichten aus der Datenbank auszulesen.
     * @param myUserID Die eigene UserID.
     * @param othersUserID Die UserID des anderen Chatteilnehmers
     */
    private void readMessages(final String myUserID, final String othersUserID){
        messages = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message m = snapshot.getValue(Message.class);
                    if(m.getReceiver().equals(myUserID) && m.getSender().equals(othersUserID) || m.getReceiver().equals(othersUserID) && m.getSender().equals(myUserID)){
                        messages.add(m);
                    }
                    chatAdapter = new ChatAdapter(Chat.this,messages,othersUserID);
                    messagesRecyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
