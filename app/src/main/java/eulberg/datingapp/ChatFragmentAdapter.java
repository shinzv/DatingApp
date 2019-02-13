package eulberg.datingapp;

import android.content.Context;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder> {

    private ArrayList<String> chatUserIDs;
    private Context context;


    public ChatFragmentAdapter(Context context, ArrayList<String> chatUserIDs) {
        this.chatUserIDs = chatUserIDs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_chat_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        //TODO Profilbild und Namen über die ID festlegen
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(chatUserIDs.get(i));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                viewHolder.chatUsername.setText(user.getUsername());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+chatUserIDs.get(i));
                long megabyte = 1024 * 1024;
                storageReference.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Glide.with(context).asBitmap().load(bytes).into(viewHolder.chatImage);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("userID",chatUserIDs.get(i));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUserIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView chatImage;
        TextView chatUsername;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatImage = itemView.findViewById(R.id.chatImage);
            chatUsername = itemView.findViewById(R.id.chatUsername);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
