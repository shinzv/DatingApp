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

public class ChatFragment extends Fragment {

    private ArrayList<String> chatUserIDs = new ArrayList<>();
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
        chatUserIDs.clear();
        chatUserIDs.add("2vbbAIacxmfe1vRx1FnWNeMH5cD2");
        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.chats);
        ChatFragmentAdapter adapter = new ChatFragmentAdapter(getContext(), chatUserIDs);
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
