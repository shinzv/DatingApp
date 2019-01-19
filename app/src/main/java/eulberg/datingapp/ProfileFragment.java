package eulberg.datingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView name;
    private TextView age;
    private TextView description;

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
        profilePicture = getView().findViewById(R.id.profile_picture);
    }
}
