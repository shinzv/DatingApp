package eulberg.datingapp;

import android.Manifest;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = Login.class.getSimpleName();
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Berechtigungen erfragen
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // User is signed in.
            startActivity(new Intent(Login.this, Home.class));
        } else {
            // No user is signed in.
        }

        RelativeLayout login = findViewById(R.id.login);
        AnimationDrawable animationDrawable = (AnimationDrawable) login.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        email = findViewById(R.id.user);
        password = findViewById(R.id.password);

        //Login Button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    signIn(email.getText().toString(), password.getText().toString());
                }else{
                    Log.d(TAG, "Es sind nicht alle Felder ausgefüllt");
                    Toast.makeText(Login.this, "Alle Felder müssen gefüllt sein", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Register Button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(String email, String password){
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
    }

    /**
     * Regelt was passieren soll, wenn man den zurück Knopf/Touch screen bereich des Handys drückt.
     */
    @Override
    public void onBackPressed(){
        //Hier soll nichts passieren.
    }
}
