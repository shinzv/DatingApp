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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<String> chatUsernames = new ArrayList<>();
    private ArrayList<String> chatImages = new ArrayList<>();
    private Context context;


    public ChatAdapter(Context context, ArrayList<String> chatUsernames, ArrayList<String> chatImages) {
        this.chatUsernames = chatUsernames;
        this.chatImages = chatImages;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context).asBitmap().load(chatImages.get(i)).into(viewHolder.chatImage);
        viewHolder.chatUsername.setText(chatUsernames.get(i));

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Chat öffnen
                context.startActivity(new Intent(context, Message.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatUsernames.size();
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
