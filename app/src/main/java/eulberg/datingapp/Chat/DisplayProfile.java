package eulberg.datingapp.Chat;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import eulberg.datingapp.R;
import eulberg.datingapp.UserSettings;

public class DisplayProfile extends AppCompatActivity {



    private CircleImageView profileImage;
    private TextView userNameAndAge;
    private TextView description;

    private String userID;

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Initialisierungen usw...
     * @param savedInstanceState der gespeicherte Status der App
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayprofile);


        profileImage = findViewById(R.id.displayprofileImage);
        userNameAndAge = findViewById(R.id.displayprofileNameAndAge);
        description = findViewById(R.id.displayprofileDescription);

        userID = getIntent().getStringExtra("userID");

        //Animiert den  Hintergrund des Profilbildes
        RelativeLayout profile = findViewById(R.id.displayProfileBackground);
        AnimationDrawable animationDrawable = (AnimationDrawable) profile.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();


        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+userID);
        long megabyte = 1024 * 1024;
        storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Glide.with(DisplayProfile.this).asBitmap().load(bytes).into(profileImage);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_settings/"+userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserSettings userSettings = dataSnapshot.getValue(UserSettings.class);
                userNameAndAge.setText(userSettings.getUsername()+ " ("+userSettings.getAge() + ")");
                description.setText(userSettings.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
