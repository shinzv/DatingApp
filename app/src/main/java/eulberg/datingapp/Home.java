package eulberg.datingapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Navigation bar
        BottomNavigationView nav = findViewById(R.id.navigation_bar);
        nav.setOnNavigationItemSelectedListener(navListener);
        //Default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiscoverFragment()).commit();


    }

    //Wechsel der Fragmente
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_chat:
                    selectedFragment =  new ChatFragment();
                    break;
                case R.id.nav_discover:
                    selectedFragment =  new DiscoverFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment =  new ProfileFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
