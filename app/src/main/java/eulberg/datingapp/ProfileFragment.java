package eulberg.datingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ProfileFragment extends Fragment {

    private ImageButton editButton;

    private ImageView profilePicture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    //TODO ProfileFragment schließt sobald onCreate drin ist
    //Solved by Haydar: OnCreateView -> OnCreate bei Fragments bedeutet: findViewById darf noch nicht angewendet werden, returnt NULL!
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editButton = getView().findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    public void start(){
        startActivity(new Intent(getActivity(), EditProfile.class));
    }

}
