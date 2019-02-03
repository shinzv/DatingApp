package eulberg.datingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;

public class ChatFragment extends Fragment {

    private ArrayList<String> chatUsernames = new ArrayList<>();
    private ArrayList<String> chatImages = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        initImageBitmaps();
    }

    private void initImageBitmaps() {
        //Bsp.:
        //chatImages.add(storageReference.child("ProfilePictures/"+mAuth.getCurrentUser().getUid()+".jpg").getDownloadUrl().toString());
        chatUsernames.clear();
        chatImages.clear();
        chatImages.add("https://firebasestorage.googleapis.com/v0/b/datingapp-65363.appspot.com/o/ProfilePictures%2FC8z7q46a4Xalu9hEBEMh5tDxAes2?alt=media&token=e9599e93-733b-41c5-a163-9ea23d57a46a");
        chatUsernames.add(mAuth.getCurrentUser().getEmail());
        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.chats);
        ChatAdapter adapter = new ChatAdapter(getContext(),chatUsernames,chatImages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Checks if the user is connected to the internet. -> This method cannot be modularised because if it is static we cannot reference to the non-static method
     * "getActivity()" which is essential for this method to work.
     * @return wether the user is connected or not.
     */
    private boolean checkInternetConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            //We are connected to a network
            connected = true;
        }
        return connected;
    }
}
