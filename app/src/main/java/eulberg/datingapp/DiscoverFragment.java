package eulberg.datingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.Utils;

public class DiscoverFragment extends Fragment {

    private static final String TAG = DiscoverFragment.class.getSimpleName();
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private DatabaseReference userSettingsReference;
    private ValueEventListener valueEventListener;

    //Userdata
    private UserSettings userSettings;
    private String userID;
    private String genderToSearchFor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeView = (SwipePlaceHolderView)getView().findViewById(R.id.swipeView);
        mContext = getContext();

        //Legt Werte der Swipecards fest
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipecard_accept)
                        .setSwipeOutMsgLayoutId(R.layout.swipecard_reject));

        //Firebase Authentifikation
        fireBaseAuth();

        //Erstellt einen ValueEventListener für die Abfrage der Daten bezüglich der Swipecards
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals("user_settings")) {
                            Log.d(TAG, "Datasnapshot: " + ds);
                            try {
                                UserSettings user = ds.getValue(UserSettings.class);
                                String ID = ds.getChildren().toString();
                                mSwipeView.addView(new Swipecard(mContext, user, ID, mSwipeView));
                            } catch (NullPointerException e) {
                                Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
            }
        };

        Query query = FirebaseDatabase.getInstance().getReference("user_settings")
                .orderByChild("gender")
                .equalTo(genderToSearchFor)
                .limitToFirst(10);

        query.addListenerForSingleValueEvent(valueEventListener);

        //Buttons zum liken oder ablehnen
        getView().findViewById(R.id.reject_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });
        getView().findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
    }

    /**
     * Checks if the user is connected to the internet. -> This method cannot be modularised because if it is static we cannot reference to the non-static method
     * "getActivity()" which is essential for this method to work.
     * @return wether the user is connected or not.
     */
    private boolean checkInternetConnection(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            //We are connected to a network
            connected = true;
        }
        return connected;
    }

    public void fireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Wenn User eingeloggt
                if (user != null) {
                    userID = mAuth.getCurrentUser().getUid();
                } else {

                }
            }
        };
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieving data
                getUserSettings(dataSnapshot);
                setGenderToSearchFor();
                mSwipeView.addView(new Swipecard(mContext, userSettings, userID, mSwipeView));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "Retrieving user_settings information from firebase");
        userSettings = new UserSettings();
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals("user_settings")){
                Log.d(TAG, "Datasnapshot: " + ds);
                try {
                    userSettings = ds.child(userID).getValue(UserSettings.class);
                }catch(NullPointerException e){
                    Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                }

            }
        }
    }

    public void setGenderToSearchFor(){
        if(userSettings.getGender() == "male"){
            genderToSearchFor = "female";
        }else{
            genderToSearchFor = "male";
        }
    }
}
