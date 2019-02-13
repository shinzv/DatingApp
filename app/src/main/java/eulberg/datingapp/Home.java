package eulberg.datingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity implements InterstitialListener {

    //Referenzen
    private LocationManager locationManager;
    private LocationListener listener;
    private String provider;

    //Tag für die Fehlerausgabe und Konsolenausgabe
    private static final String TAG = Home.class.getSimpleName();

    private ChatFragment chatFragment = new ChatFragment();
    private DiscoverFragment discoverFragment = new DiscoverFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private String userID;

    private UserSettings userSettings;

    //IronSource
    private final String APP_KEY = "88c7a19d";
    private boolean stopAds = true;

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Initialisierungen usw...
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Navigation bar
        BottomNavigationView nav = findViewById(R.id.navigation_bar);
        nav.setOnNavigationItemSelectedListener(navListener);
        //Default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, chatFragment).commit();


        checkIfGPSIsEnabled();

        /*
        RelativeLayout home = findViewById(R.id.homeBackground);
        AnimationDrawable animationDrawable = (AnimationDrawable) home.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        */

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();


        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //LocationManager-Instanz ermitteln
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //Liste mit den Namen aller Providern erfragen
            List<String> providers = locationManager.getAllProviders();
            for (String name : providers) {
                LocationProvider lp = locationManager.getProvider(name);
                Log.d(TAG, lp.getName() + " --- isProviderEnabled(): "
                        + locationManager.isProviderEnabled(name));

                Log.d(TAG, "requiresCell(): " + lp.requiresCell());
                Log.d(TAG, "requiresNetwork: " + lp.requiresNetwork());
                Log.d(TAG, "requiresSatellite(): " + lp.requiresSatellite());
            }
            //Provider mit ...
            Criteria criteria = new Criteria();
            // ... grober Genauigkeit ...
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            //... und geringem Akku-Verbrauch.
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            provider = locationManager.getBestProvider(criteria, true);
            Log.d(TAG, provider);

            //LocationListener-Objekt
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged()");
                    if (location != null) {
                        String s = "Breite: " + location.getLatitude() + "\nLänge: " + location.getLongitude();
                        Log.d(TAG, s);
                        Geocoder geocoder = new Geocoder(Home.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            String stadtName = addresses.get(0).getLocality();
                            reference.child("user_settings").child(userID).child("city").setValue(stadtName);
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    Log.d(TAG, "onStatusChanged()");
                }

                @Override
                public void onProviderEnabled(String s) {
                    Log.d(TAG, "onProviderEnabled()");
                }

                @Override
                public void onProviderDisabled(String s) {
                    Log.d(TAG, "onProviderDisabled()");
                }
            };
        }

        if(!stopAds) {
            IronSource.setInterstitialListener(this);
            /**
             *Ad Units should be in the type of IronSource.Ad_Unit.AdUnitName, example
             */
            IronSource.init(this, APP_KEY, IronSource.AD_UNIT.INTERSTITIAL);

            IntegrationHelper.validateIntegration(this);
            IronSource.getAdvertiserId(Home.this);

            //show the interstitial
            IronSource.loadInterstitial();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    IronSource.showInterstitial();
                }
            }, 5000);
        }



    }

    /**
     * Prüft, ob das GPS an ist, wenn nicht soll der User es anschalten.
     */
    private void checkIfGPSIsEnabled() {
        try {
            int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
                alertDialog.setTitle("GPS Einstellung!");
                alertDialog.setMessage("Your GPS is off. Do you want to go to the settings? ");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Schliesst die app
                        finish();
                        System.exit(0);
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        } catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }
    }

    //Wechsel der Fragmente
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_chat:
                    selectedFragment = chatFragment;
                    break;
                case R.id.nav_discover:
                    selectedFragment = discoverFragment;
                    break;
                case R.id.nav_profile:
                    selectedFragment =  profileFragment;
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    /**
     * Regelt was passieren soll, wenn man den zurück Knopf/Touch screen bereich des Handys drückt.(In diesem Fall soll nichts passieren.)
     */
    @Override
    public void onBackPressed(){
        //Hier soll nichts passieren.
    }

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Fügt den LocationListener hinzu.(Jede Minute wird er aufgerufen.)
     */
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart()");
        //Überprüft, ob die Berechtigung gegeben wurde.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Ruft den Listener jede Minute auf.
            locationManager.requestLocationUpdates(provider, 100, 0, listener);
        } else{
            Log.d(TAG,"GPS permissions denied.");
        }
    }

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Entfernt den LocationListener.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.removeUpdates(listener);
        }
    }

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * Nach 5 Sekunden wird nochmal geprüft, ob das GPS eingeschaltet wurde. Wenn nicht, dann wird CheckIfGPSEnabled() aufgerufen.
     */
    @Override
    public void onResume(){
        super.onResume();
        IronSource.onResume(this);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int state = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                    if (state == 0) {
                        checkIfGPSIsEnabled();
                    }
                } catch (Settings.SettingNotFoundException e){
                    e.printStackTrace();
                }
            }
        }, 10000);

    }

    /**
     * Siehe „Lifecyle of Activity“ für den Aufrufszeitraum.
     * IronSource.onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }


    //
    //InterStitial Listener: START
    //
    @Override
    public void onInterstitialAdReady() {

    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

    }

    @Override
    public void onInterstitialAdOpened() {

    }

    @Override
    public void onInterstitialAdClosed() {

    }

    @Override
    public void onInterstitialAdShowSucceeded() {

    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

    }

    @Override
    public void onInterstitialAdClicked() {

    }
    //
    //InterStitial Listener: END
    //

}
