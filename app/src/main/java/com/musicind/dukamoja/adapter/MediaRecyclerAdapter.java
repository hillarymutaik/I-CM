package com.musicind.dukamoja.adapter;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.async.CustomItemClickListener;
import com.musicind.dukamoja.async.DownloadBinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<MediaRecyclerAdapter.MediaViewHolder> {
    //private ArrayList<RecyclerData> courseDataArrayList;
    private List<MediaMetadataCompat> mediaList ;
    //private ClckListener<RecyclerData> clickListener;
    private OnItemClickListener listener;
    private int selectedIndex;
    private Context mcontext;
    private LottieAnimationView downloadAnimation;
    DownloadManager downloadManager;
    long downloadReference = 0;
    int DOWNLOAD_NOTIFICATION_ID = 234;
    boolean isDownloading = false;
    private CustomItemClickListener clickListener;

    private ProgressDialog pDialog;
    ImageView my_image;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    //itemclick listerner


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setClickListener(CustomItemClickListener li) {
        this.clickListener = li;
    }

    public MediaRecyclerAdapter(List<MediaMetadataCompat> recyclerDataArrayList, Context mcontext, CustomItemClickListener clickListener) {
        this.mediaList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.clickListener = clickListener;
        selectedIndex = -1;
    }

    @NonNull
    @Override
    public MediaRecyclerAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new MediaViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MediaRecyclerAdapter.MediaViewHolder holder, int position) {

        holder.title.setText(mediaList.get(position).getDescription().getTitle().toString());
        //holder.descriptionTextView.setText(Objects.requireNonNull(mediaList.get(position).getDescription().getSubtitle()).toString());
        //TODO make sure of this data type of date
        holder.timeStampTextView.setText(mediaList.get(position).getMediaMetadata().toString());


        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mcontext, Objects.requireNonNull(mediaList.get(position).getDescription().getMediaUri()).toString(), Toast.LENGTH_SHORT).show();
                clickListener.onItemClick(Objects.requireNonNull(mediaList.get(position).getDescription().getMediaUri()).toString());
            }
        });

        if(selectedIndex == position){
            holder.title.setTextColor(ContextCompat.getColor(mcontext,R.color.colorPrimary));
            SharedPreferences sharedPref = mcontext.getSharedPreferences("application", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("SONG_ID", Integer.parseInt(Objects.requireNonNull(mediaList.get(position).getDescription().getMediaId())));
            editor.apply();
        }else{
            holder.title.setTextColor(ContextCompat.getColor(mcontext,R.color.black));
        }


    }

    public void setSelectedIndex(int index){
        selectedIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }

    public int getIndexOfItem(MediaMetadataCompat mediaItem){
        for(int i=0;i<mediaList.size();i++){
            if(mediaList.get(i).getDescription().getMediaId().equals(mediaItem.getDescription().getMediaId())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView title,
                timeStampTextView;
               // descriptionTextView;
        ImageView image, download;;
        private CardView cardView;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timeStampTextView = itemView.findViewById(R.id.media_time_stamp);
            image = itemView.findViewById(R.id.img1);
            cardView = itemView.findViewById(R.id.cardview);
            download = itemView.findViewById(R.id.download);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(position);
                    }
                }
            });

        }

    }



//    public void setOnItemClickListener(ClckListener<RecyclerData> dataClick) {
//        this.clickListener = dataClick;
//    }
}


