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

import java.util.ArrayList;

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

    //Matching
    private ArrayList<LikedUser> likedUsers;

    /**
     * Wird aufgerufen beim Erstellen der Activity, hier wird das Layout wird festgelegt
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    /**
     * Wird aufgerufen beim Erstellen der Activity, hier wird die SwipeView für die Swipecards instanziiert,
     * sowie Methoden mit hoher Priorität aufgerufen und die Buttons angelegt.
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeView = (SwipePlaceHolderView)getView().findViewById(R.id.swipeView);
        mContext = getContext();
        genderToSearchFor = "male";

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

        userSettingsReference = firebaseDatabase.getInstance().getReference().child("user_settings");

        getLikes();
        getPotentialMatches();

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

    /**
     * Firebase Authentifikation.
     * Hier werden die jeweiligen Firebasen Instanzen initialisiert. Dies umfasst eine Instanz der Firebase Datenbank,
     * eine Referenz der Datenbank, sowie die ID des authentifizierten Nutzers. Außerdem wird die Methode getUserSettings() hier aufgerufen,
     * da sie einen Datasnapshot benötigt von der Referenz benötigt.
     */
    public void fireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieving user data
                getUserSettings(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Nutzerdaten werden aus dem snapshot der Datenbank in ein Objekt der Klasse UserSettings geladen und die Methode
     * setGenderToSearchFor() wird aufgerufen
     * @param dataSnapshot Snapshot der Datenbank
     */
    public void getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "Retrieving user_settings information from Firebase");
        userSettings = new UserSettings();
        for (DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals("user_settings")){
                Log.d(TAG, "Datasnapshot: " + ds);
                try {
                    userSettings = ds.child(userID).getValue(UserSettings.class);
                    setGenderToSearchFor();
                }catch(NullPointerException e){
                    Log.d(TAG, "Error occurred loading data: " + e.getMessage());
                }

            }
        }
    }

    /**
     * Mit einer Query wird ein Snapshot der "likes" des aktiven Nutzers gemacht und diese werden in Form eines Objekts der Klasse likedUsers in eine ArrayList eingefügt.
     */
    public void getLikes(){
        Log.d(TAG, "Retrieving liked users from Firebase");
        likedUsers = new ArrayList<>();
        Query liked = reference.child("likes").child(userID).orderByChild("likedUserID");
        liked.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    LikedUser likedUser = snapshot.getValue(LikedUser.class);
                    likedUsers.add(likedUser);
                    Log.d(TAG, likedUsers.get(0).getLikedUserID());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Es wird das jeweils andere Geschlecht, als das zu suchende Geschlecht gesetzt.
     * Die App ist zum jetzigen Zeitpunkt nur auf hetero Nutzer ausgelegt.
     */
    public void setGenderToSearchFor(){
        if(userSettings.getGender().equals("male")){
            genderToSearchFor = "female";
        }else{
            genderToSearchFor = "male";
        }
    }

    /**
     * Diese Methode sucht alle potentiellen Matches aus der Datenbank und filtert sie.
     * Die Query beschränkt das Geschlecht der User innerhalb des Snapshots. Exkurs: Firebase Queries können nur eine Bedingung haben,
     * also filtert man danach womit man die meisten User ausschließen kann. Danach wird durch den Snapshot iteriert und falls sie nicht bereits
     * vom aktiven Nutzer geliket wurden, also sich in der likedUsers ArrayList befinden, werden sie in einer Swipecard zur Swipeview hinzugefügt.
     *
     */
    public void getPotentialMatches(){
        Query potentialMatches = userSettingsReference.orderByChild("gender").startAt(genderToSearchFor).endAt(genderToSearchFor);

        potentialMatches.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                    for (DataSnapshot ds : users){
                        boolean notLiked = true;
                        UserSettings user = ds.getValue(UserSettings.class);
                        String ID = ds.getKey();
                        for (int i = 0; i < likedUsers.size() && notLiked; i++) {
                            if(likedUsers.get(i).getLikedUserID().equals(ID)){ notLiked = false; }
                        }
                        if(notLiked) {
                            mSwipeView.addView(new Swipecard(mContext, user, ID, mSwipeView));
                        }
                    }
                }else{
                    Log.d(TAG, "Snapshot doesn't exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
