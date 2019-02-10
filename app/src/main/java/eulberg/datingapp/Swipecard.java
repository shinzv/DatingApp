package eulberg.datingapp;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.swipecard_name)
    private TextView name;

    @View(R.id.swipecard_location)
    private TextView location;

    private UserSettings user;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public Swipecard(Context context, UserSettings user, SwipePlaceHolderView swipeView) {
        mContext = context;
        this.user = user;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        //Glide.with(mContext).load(user.getImageUrl()).into(profileImageView);
        name.setText(user.getUsername()+ "(" + user.getAge() + ")");
        location.setText("Deutschland");
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
}