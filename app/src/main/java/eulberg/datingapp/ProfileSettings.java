package eulberg.datingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileSettings extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesettings);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSettings.this, Login.class));
                mAuth.signOut();
                //TODO für Haydar: Settings-File löschen
            }
        });

        Button deleteProfileButton = findViewById(R.id.deleteProfileButton);
        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid()).delete();
                databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
                databaseReference.child("user_settings").child(mAuth.getCurrentUser().getUid()).removeValue();
                //Entfernt den User aus der Authentication Liste
                mAuth.getCurrentUser().delete();
                mAuth.signOut();
                startActivity(new Intent(ProfileSettings.this, Login.class));
            }
        });


    }
}
