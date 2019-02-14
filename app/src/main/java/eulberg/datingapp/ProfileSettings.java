package eulberg.datingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;


public class ProfileSettings extends AppCompatActivity {

    //Referenzen
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Initialisierungen usw...
     * @param savedInstanceState
     */
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
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                //TODO für Haydar: Settings-File löschen
            }
        });

        Button deleteProfileButton = findViewById(R.id.deleteProfileButton);
        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    storageReference.child("ProfilePictures/" + mAuth.getCurrentUser().getUid()).delete();
                    databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).removeValue();
                    databaseReference.child("user_settings").child(mAuth.getCurrentUser().getUid()).removeValue();
                    //Entfernt den User aus der Authentication Liste
                    mAuth.getCurrentUser().delete();
                    mAuth.signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    Toast.makeText(getBaseContext(),"Account wurde gelöscht", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ProfileSettings.this, Login.class));
                }catch (Exception e){
                    Toast.makeText(getBaseContext(),"Account konnte nicht gelöscht werden", Toast.LENGTH_LONG).show();
                }
            }
        });

        Toolbar myToolbar = findViewById(R.id.my_toolbar_profile_settings);
        setSupportActionBar(myToolbar);

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    /**
     * Gibt dem Back-Button eine Funktion(zurück zum Homescreen)
     * @param item das ausgewählte item
     * @return ergebnis.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
