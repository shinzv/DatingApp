package eulberg.datingapp.Chat;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import eulberg.datingapp.Message;
import eulberg.datingapp.R;

/**
 * ChatAdapter wird benötigt um die RecyclerView für die Nachrichten im Chat zu verwalten.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT= 1;
    private ArrayList<Message> messages;
    private Context context;
    private String userID;

    private FirebaseUser firebaseUser;

    /**
     * Dem Konstruktor werden Daten übergeben, welche benötigt werden um die "Liste" mit den Chats in der RecyclerView korrekt anzuzeigen.
     * @param context Der übergebene Kontext wird benötigt um das heruntergeladene Profilbild in die dafür vorgesehene View zu laden.
     * @param messages Die ArrayList beinhaltet die Nachrichten, welche zu dem jeweiligen Chat gehören.
     * @param userID Die UserID des anderen Chatteilnehmer wird übergeben um sein Bild aus der Storage zu laden.
     */
    public ChatAdapter(Context context, ArrayList<Message> messages, String userID) {
        this.messages = messages;
        this.context = context;
        this.userID = userID;
    }

    /**
     * Das Layout für die einzelnen Nachrichten wird festgelegt, wobei unterschieden wird, ob die Nachricht von einem selber oder dem anderen Chatteilnehmer abgeschickt wurde.
     * @param viewGroup ViewGroup wird beötigt um den Kontext abzufragen, damit das korrekte Layout gesetzt werden kann.
     * @param i I wird benötigt um abzufragen, ob die Nachricht von einem selber abgeschickt wurde oder ob sie von dem anderen Chatteilnehmer abgeschickt wurde.
     * @return ViewHolder wird zurückgegeben, welcher entweder das Layout für eine von einem selber abgeschickte Nachricht beinhaltet oder das Layout für eine vom anderen Chatteilnehmer abgeschickte Nachricht beinhaltet.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item_right, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item_left, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
    }

    /**
     * Das Profilbild und die Nachricht selber werden in das aktuelle Item der RecyclerView geladen.
     * @param viewHolder ViewHolder wird benötigt um den Text der Nachricht und das Profilbild zu setzen.
     * @param i I wird benötigt um den korrekten Index für die Liste mit den Nachrichten zu übergeben.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Message message = messages.get(i);
        viewHolder.showMessage.setText(message.getMessage());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+userID);
        long megabyte = 1024 * 1024;
        storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Glide.with(context).asBitmap().load(bytes).into(viewHolder.messageItemImage);
            }
        });
    }


    /**
     * Die Länge der Liste mit den Nachrichten wird zurückgegeben.
     * @return Die Länge der Liste.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Die innere Klasse ViewHolder beinhaltet die Referenzen auf die Views des Textfeldes für die Nachricht und die des Profilbildes.
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView showMessage;
        CircleImageView messageItemImage;

        /**
         * Die Referenzen auf die Views werden initialisiert.
         * @param itemView ItemView wird übergeben um den Konstruktor der Oberklasse aufzurufen.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            messageItemImage = itemView.findViewById(R.id.messageItemImage);
        }
    }

    /**
     * Es wird überprüft, ob die aktuelle Nachricht aus der Liste mit den Nachrichten, von einem selber verschickt wurde oder ob sie von dem anderen Chatteilnehmer verschickt wurde.
     * @param position Die Position des aktuellen Index in der Liste mit den Nachrichten wird übergeben.
     * @return Es wird zurückgegeben, ob es eine Nachricht ist, welche von einem selber verschickt wurde oder ob sie von dem anderen Chatteilnehmer verschickt wurde.
     */
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
