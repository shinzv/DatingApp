package eulberg.datingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        View layoutTop = findViewById( R.id.rel_top );
        ImageButton backButton = layoutTop.findViewById(R.id.backArrow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this,Home.class);
                startActivity(intent);
            }
        });
    }

}
