package eulberg.datingapp.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import eulberg.datingapp.Discover.MatchedUser;
import eulberg.datingapp.R;

/**
 * Das ChatFragment wird dazu benutzt die Personen dazustellen, welche ich "geliket" habe und welche mich auch "zurück geliket" haben, und bietet die Funktion mit diesen dann zu schreiben.
 */
public class ChatFragment extends Fragment {

    private ArrayList<String> chatUserIDs = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    /**
     * In der Methode wird das Layout festgelegt.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    /**
     * Die Methode initialisiert die Referenzen.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        loadChatUser();
        initRecyclerView();
        if(!chatUserIDs.isEmpty()){
            getView().findViewById(R.id.chatFragmentText).setWillNotDraw(true);
        }
    }

    /**
     * Die Methode initialisiert die RecyclerView und legt den ChatFragmentAdapter als Adapter für die RecyclerView fest.
     */
    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.chats);
        ChatFragmentAdapter adapter = new ChatFragmentAdapter(getContext(), chatUserIDs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Die Methode wird benutzt um alle User aus der Datenbank auszulesen mit denen ich ein Match habe.
     */
    private void loadChatUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference matched = databaseReference.child("matches").child(firebaseUser.getUid());
        matched.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatUserIDs.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MatchedUser matchedUser = snapshot.getValue(MatchedUser.class);
                    chatUserIDs.add(matchedUser.getMatchedUserID());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
