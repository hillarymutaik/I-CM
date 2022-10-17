package com.musicind.dukamoja.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musicind.dukamoja.R;
import com.musicind.dukamoja.models.VideoDetails;
import com.musicind.dukamoja.ui.VideoPlayActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideosAdapter extends BaseAdapter {
    Activity activity;
    //    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ArrayList<VideoDetails> videoDetailsArrayList;

    LayoutInflater inflater;

    public VideosAdapter(Activity activity, ArrayList<VideoDetails> videoDetailsArrayList) {
        this.activity=activity;
        this.videoDetailsArrayList = videoDetailsArrayList;

    }

    @Override
    public int getCount() {
        return this.videoDetailsArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return videoDetailsArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(inflater==null)
        {
            inflater= this.activity.getLayoutInflater();
        }

        if(convertView==null)
        {
            convertView= inflater.inflate(R.layout.video_item,null);
        }

        ImageView imageView= convertView.findViewById(R.id.imageView);
        TextView textView= convertView.findViewById(R.id.mytitle);
        LinearLayout linearLayout= (LinearLayout)convertView.findViewById(R.id.root);
        final VideoDetails videoDetails= (VideoDetails) this.videoDetailsArrayList.get(position);



        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(activity, VideoPlayActivity.class);
                intent.putExtra("videoId", videoDetails.getVideoId());
                activity.startActivity(intent);



            }
        });



        Picasso.get().load(videoDetails.getUrl()).into(imageView);
        textView.setText(videoDetails.getTitle());
        return convertView;
    }
}

