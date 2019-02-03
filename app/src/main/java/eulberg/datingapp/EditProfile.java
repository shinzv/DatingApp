package eulberg.datingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText, ageEditText, descriptionEditText, emailEditText;
    private FloatingActionButton saveDataFab;

    private User user;
    private UserSettings userSettings;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private static final String TAG = ProfileSettings.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        //Umwandeln des Byte-Streams
        userSettings = (UserSettings) getIntent().getSerializableExtra("serialized_data_user_settings");

        //Initialize all views
        nameEditText = findViewById(R.id.username);
        ageEditText = findViewById(R.id.age);
        descriptionEditText = findViewById(R.id.description);
        emailEditText = findViewById(R.id.email);
        saveDataFab = findViewById(R.id.save_data);

        saveDataFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Add back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        load();

    }

    private void load(){

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieving data
                getUser(dataSnapshot);
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *
     * @param item das ausgewählte item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Lädt die Daten aus dem User-Objekt und dem UserSettings-Objekt.
     */
    private void loadData(){
        //nameEditText, ageEditText, descriptionEditText, emailEditText, phoneNumberEditText;

        nameEditText.setText(userSettings.getUsername());
        ageEditText.setText(String.valueOf(userSettings.getAge()));
        descriptionEditText.setText(userSettings.getDescription());
        emailEditText.setText(user.getEmail());

        /*
        //Mode private bedeutet, dass keine andere App auf unsere SharedPreferences zugreifen darf.
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //Second argument is the default value(Will be set if nothing has been found.)
        nameEditText.setText(sharedPreferences.getString(name, ""));
        ageEditText.setText(sharedPreferences.getString(age, ""));
        descriptionEditText.setText(sharedPreferences.getString(description, ""));
        emailEditText.setText(sharedPreferences.getString(email, ""));
        phoneNumberEditText.setText(sharedPreferences.getString(phoneNumber, ""));
        */
    }

    /**
     * Speichert die daten online ab.
     */
    private void saveData(){
        user.setUsername(nameEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());
        userSettings.setUsername(nameEditText.getText().toString());
        userSettings.setAge(Integer.valueOf(ageEditText.getText().toString()));
        userSettings.setDescription(descriptionEditText.getText().toString());
        Toast.makeText(this, "SAVE DATA", Toast.LENGTH_SHORT).show();
        reference.child("users").child(user.getUser_id()).setValue(user);
        reference.child("user_settings").child(user.getUser_id()).setValue(userSettings);

        /*
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Save variables.
        editor.putString(name, nameEditText.getText().toString());
        editor.putString(age, ageEditText.getText().toString());
        editor.putString(description, descriptionEditText.getText().toString());
        editor.putString(email, emailEditText.getText().toString());
        editor.putString(phoneNumber, phoneNumberEditText.getText().toString());
        //Writes the data synchronouslly(commit()) -> may bring ui rendering to a break, apply() -> still lets the ui render but is asynchronous
        editor.apply();
        Toast.makeText(EditProfile.this, "SAVED", Toast.LENGTH_SHORT).show();
        */

    }

    /**
     * Lädt die Daten des Users runter und speichert diese im User-Objekt
     * @param dataSnapshot DataSnapshot der aktuellen Datenbank.
     */
    public void getUser(DataSnapshot dataSnapshot){

        Log.d(TAG, "Retrieving user_settings information from firebase");

        user = new User();

        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals("users")){
                Log.d(TAG, "Datasnapshot: " + ds);

                try {
                    user = ds.child(mAuth.getUid()).getValue(User.class);
                }catch(NullPointerException e){
                    Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                }

            }
        }

    }

}
