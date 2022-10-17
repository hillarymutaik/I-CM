package com.musicind.dukamoja.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicind.dukamoja.R;
import com.musicind.dukamoja.models.CommentModel;
import com.musicind.dukamoja.models.RecyclerData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    ArrayList<CommentModel> commentModel;

    public CommentAdapter(Context context, ArrayList<CommentModel> commentModel) {
        this.context = context;
        this.commentModel = commentModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentModel comment = commentModel.get(position);
        holder.username.setText(comment.getUserName());
        //holder.profile_image.setImageResource(comment.getUserImage());
        holder.comment.setText(comment.getComment());

    }

    @Override
    public int getItemCount() {
        return commentModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }

}
