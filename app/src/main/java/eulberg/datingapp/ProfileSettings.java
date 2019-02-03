package eulberg.datingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileSettings extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilesettings);
        mAuth = FirebaseAuth.getInstance();


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
                deleteProfile();
            }
        });


    }

    private void deleteProfile() {

    }


    /*private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in erfolgreich
                            Log.d(TAG, "sign in erfolgreich");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this,Home.class);
                            startActivity(intent);
                        } else {
                            //Sign in fehlgeschlagen
                            Log.w(TAG, "sign in fehlgeschlagen", task.getException());
                            Toast.makeText(Login.this, "Anmeldung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/
}
