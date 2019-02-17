package eulberg.datingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.ArrayList;
import java.util.HashMap;

@Layout(R.layout.swipecard)
public class Swipecard {

    private static final String TAG = DiscoverFragment.class.getSimpleName();

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.swipecard_name)
    private TextView name;

    @View(R.id.swipecard_location)
    private TextView location;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private String currentUserID;

    private UserSettings user;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String ID;

    public Swipecard(Context context, UserSettings user, String ID, SwipePlaceHolderView swipeView) {
        mContext = context;
        this.user = user;
        mSwipeView = swipeView;
        this.ID = ID;
    }

    /**
     * Methode wird aufgerufen, sobald die Swipecard erstellt wurde.
     * Hier wird das Profilbild, sowie alle Informationen der Swipecard gesetzt.
     */
    @Resolve
    private void onResolved(){
        //Glide.with(mContext).load(user.getImageUrl()).into(profileImageView);
        loadImage();
        name.setText(user.getUsername()+ "(" + user.getAge() + ")");
        location.setText(user.getCity());
        Log.d(TAG, "Swipecard created with ID: " + ID);
        fireBaseAuth();
    }
    /**
     * Methode wird aufgerufen, wenn nach links geswiped wurde.
     */
    @SwipeOut
    private void onSwipedOut(){
        Log.d("SWIPE", "SwipedOut: Reject");
        mSwipeView.addView(this);
    }

    /**
     * Methode wird aufgerufen, wenn nach rechts geswiped wurde.
     * Die ID des geliketen wird in der Datenbank innerhalb der Node des aktiven Nutzers als "liked" gespeichert
     * und es wird die Methode checkIfMatch aufgerufen.
     */
    @SwipeIn
    private void onSwipeIn(){
        Log.d("SWIPE", "SwipedIn: Accept");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("likedUserID", ID);
        reference.child("likes").child(currentUserID).push().setValue(hashMap);
        checkIfMatch();
    }

    /**
     * Wird aufgerufen wenn man einen Swipe abbricht
     */
    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("SWIPE", "SwipeCancelled");
    }

    /**
     * Wird aufgerufen wenn man die Card nach Rechts bewegt.
     */
    @SwipeInState
    private void onSwipeInState(){
        Log.d("SWIPE", "onSwipeInState");
    }

    /**
     * Wird aufgerufen wenn man die Card nach Links bewegt.
     */
    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("SWIPE", "onSwipeOutState");
    }

    public String getID(){
        return ID;
    }

    private void loadImage(){
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/" + ID);
            long megabyte = 1024 * 1024;
            storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(mContext).asBitmap().load(bytes).into(profileImageView);
                }
            });
        }catch(Exception e){
            Log.d(TAG, "Couldn't load image" + ID);
        }
    }

    public void checkIfMatch(){
        Query liked = reference.child("likes").child(ID).orderByChild("ID");
        liked.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    LikedUser likedUser = snapshot.getValue(LikedUser.class);
                    if(likedUser.getLikedUserID().equals(currentUserID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("matchedUserID", ID);
                        reference.child("matches").child(currentUserID).push().setValue(hashMap);
                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("matchedUserID", currentUserID);
                        reference.child("matches").child(ID).push().setValue(hashMap);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void fireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
    }
}