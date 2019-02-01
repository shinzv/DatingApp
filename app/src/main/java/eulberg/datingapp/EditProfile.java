package eulberg.datingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class EditProfile extends AppCompatActivity {

    private EditText nameEditText, ageEditText, descriptionEditText, emailEditText, phoneNumberEditText;
    private FloatingActionButton saveDataFab;

    //SharedPreferences
    public static final String SHARED_PREFS = "SharedPrefs";

    public static final String name = "Name";
    public static final String age = "Age";
    public static final String description = "Description";
    public static final String email = "Email";
    public static final String phoneNumber = "Phone number";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);


        //Initialize all views
        nameEditText = findViewById(R.id.username);
        ageEditText = findViewById(R.id.age);
        descriptionEditText = findViewById(R.id.description);
        emailEditText = findViewById(R.id.email);
        phoneNumberEditText = findViewById(R.id.phoneNumber);
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

        loadData();
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
     * Lädt die Daten aus der abgespeicherten SharedPreference Datei.
     */
    private void loadData(){
        //Mode private bedeutet, dass keine andere App auf unsere SharedPreferences zugreifen darf.
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        //Second argument is the default value(Will be set if nothing has been found.)
        nameEditText.setText(sharedPreferences.getString(name, ""));
        ageEditText.setText(sharedPreferences.getString(age, ""));
        descriptionEditText.setText(sharedPreferences.getString(description, ""));
        emailEditText.setText(sharedPreferences.getString(email, ""));
        phoneNumberEditText.setText(sharedPreferences.getString(phoneNumber, ""));

    }

    /**
     * Speichert die daten online und offline ab.
     */
    private void saveData(){
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

    }

}
