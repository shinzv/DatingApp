package eulberg.datingapp;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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

@Layout(R.layout.swipecard)
public class Swipecard {

    private static final String TAG = DiscoverFragment.class.getSimpleName();

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.swipecard_name)
    private TextView name;

    @View(R.id.swipecard_location)
    private TextView location;

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

    @Resolve
    private void onResolved(){
        //Glide.with(mContext).load(user.getImageUrl()).into(profileImageView);
        loadImage();
        name.setText(user.getUsername()+ "(" + user.getAge() + ")");
        location.setText(user.getCity());
        Log.d(TAG, "Swipecard created with ID: " + ID);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
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
}