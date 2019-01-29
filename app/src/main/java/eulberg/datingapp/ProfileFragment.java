package eulberg.datingapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.net.URI;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private ImageButton editButton;

    //For taking a picture.
    private static final String TITLE = "DatingApp";
    private static final String DESCRIPTION = "Picture was took with DATINGAPP!";

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final int IMAGE_CAPTURE = 1;

    //Views
    private ImageView profilePicture;

    //Over this URI is the image accesable
    private Uri imageURI;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private UserSettings userSettings;
    private User user;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    //TODO ProfileFragment schließt sobald onCreate drin ist
    //Solved by Haydar: OnCreateView -> OnCreate bei Fragments bedeutet: findViewById darf noch nicht angewendet werden, returnt NULL!
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editButton = getView().findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });


        //fix the portrait mode
        //Not able to do this in a class that extends Fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Nicht nötig? Fragment ist mit einer Activity assoziiert
        //getActivity().setContentView(R.layout.main);

        profilePicture = getView().findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        fireBaseAuth();
    }

    private void startCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, TITLE);
        values.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        imageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                try{
                    Bitmap b1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
                    //Größe des aufgenommenen Bildes
                    float w1 = b1.getWidth();
                    float h1 = b1.getHeight();
                    profilePicture.setImageBitmap(b1);
                }catch (IOException e) { //und FileNotFoundException

                }
            }else{
                int rowsDeleted = getActivity().getContentResolver().delete(imageURI, null, null);
                Log.d(TAG, rowsDeleted + " rows deleted");
            }
        }
    }

    public void fireBaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Wenn User eingeloggt
                if (user != null) {
                    userID = mAuth.getCurrentUser().getUid();
                } else {

                }
            }
        };

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieving data
                getUserSettings(dataSnapshot);
                setProfileInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "Retrieving user information from firebase");

        userSettings =  new UserSettings();

        user = new User();

        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals(ProfileFragment.this.getString(R.string.db_user_settings))){
                Log.d(TAG, "Datasnapshot: " + ds);

                try {
                    userSettings = ds.child(userID).getValue(UserSettings.class);
                    /*
                    userSettings.setUsername(ds.child(userID).getValue(UserSettings.class).getUsername());
                    userSettings.setDescription(ds.child(userID).getValue(UserSettings.class).getDescription());
                    userSettings.setProfile_picture(ds.child(userID).getValue(UserSettings.class).getProfile_picture());
                    */
                }catch(NullPointerException e){
                    Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                }


            }
        }

    }


    public void setProfileInfo(){
        TextView username = getView().findViewById(R.id.name);
        username.setText(userSettings.getUsername());
    }


}
