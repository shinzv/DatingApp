package eulberg.datingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = Register.class.getSimpleName();
    private EditText email, password, password2, username;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Context mContext;

    private String userID;

    //Gender
    private CircleImageView male;
    private CircleImageView female;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RelativeLayout register = findViewById(R.id.register);
        AnimationDrawable animationDrawable = (AnimationDrawable) register.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        fireBaseAuth();
        mContext = Register.this;

        email = findViewById(R.id.user);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        username = findViewById(R.id.username);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        //Male and Female Button function
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "male";
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "female";
            }
        });

        //Register Button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alle Felder müssen befüllt sein
                if(!password2.getText().equals("") || !password.getText().equals("") || !username.getText().equals("") || !email.getText().equals("")) {
                    //Passwort Überprüfungen
                    if (password.getText().toString().equals(password2.getText().toString())) {
                        if(password.getText().toString().matches(".*\\d+.*")) {
                            //E-Mail Überprüfungen
                            if(email.getText().toString().contains("@") && email.getText().toString().split("@")[1].contains(".")){
                                //Überprüfe, ob ein Geschlecht angegeben wurde.
                                if(gender != null) {
                                    signUp(email.getText().toString(), password.getText().toString());
                                } else{
                                    Toast.makeText(Register.this, "Gib dein Geschlecht an", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(Register.this, "Gib bitte eine gültige E-Mail Adresse an", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Register.this, "Passwort muss mindestens eine Ziffer enthalten", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register.this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Sign up erfolgreich
                    Log.d(TAG, "createUser:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    userID = mAuth.getCurrentUser().getUid();
                    //Neuen Nutzer zur Datenbank hinzufügen
                    addNewUser(getEmail(), getUsername(), "Ziemlich leer hier.", gender);

                    Intent intent = new Intent(Register.this, Home.class);
                    startActivity(intent);
                } else {
                    //Sign up fehlgeschlagen
                    Log.w(TAG, "createUser:failed", task.getException());
                    Toast.makeText(Register.this, "Registration fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void fireBaseAuth() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Wenn User eingeloggt
                if (user != null) {
                    Log.d(TAG, "User eingeloggt: " + user.getUid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.d(TAG, "User ausgeloggt");

                }
            }
        };
    }

    public void addNewUser(String email, String username, String description, String gender){
        User user = new User(userID, username, email);
        reference.child(mContext.getString(R.string.db_users)).child(userID).setValue(user);

        UserSettings settings = new UserSettings(0, description, gender, username);
        reference.child(mContext.getString(R.string.db_user_settings)).child(userID).setValue(settings);
    }

    public String getEmail(){
        return email.getText().toString();
    }
    public String getUsername(){
        return username.getText().toString();
    }
}

