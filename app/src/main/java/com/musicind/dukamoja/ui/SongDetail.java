package com.musicind.dukamoja.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.CommentAdapter;
import com.musicind.dukamoja.dialog.CommentDialog;
import com.musicind.dukamoja.models.CommentModel;
import com.musicind.dukamoja.models.RecyclerData;

import java.util.ArrayList;

public class SongDetail extends AppCompatActivity {
    private TextView projectTitle;
    private ImageView backBtn;
    private WebView webView;
    private RecyclerView recyclerView;
    private TextView projectComments, songLyrics;
    private FloatingActionButton writeComment;
    ArrayList<CommentModel> commentModels;
    private AppCompatButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        projectTitle = findViewById(R.id.projectTitle);
        backBtn = findViewById(R.id.backBtn);
        webView = findViewById(R.id.projectDetailsWebView);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        projectComments = findViewById(R.id.songComments);
        songLyrics = findViewById(R.id.songLyrics);
        writeComment = findViewById(R.id.writeComment);
        actionButton = findViewById(R.id.actionButton);

        String r1 = "#EAEDF1";
//        int color = android.graphics.Color.parseColor(r1);
//        songLyrics.setBackgroundColor(color);
       String clo = "#4D000000";
        int n = Color.parseColor(clo);
       projectComments.setTextColor(n);
        String white = "#FFFFFF";
        String r0 = "#99000000";
        int r01 = android.graphics.Color.parseColor(r0);
        int n1 = Color.parseColor(clo);
        int n2 = Color.parseColor(white);
        int color = android.graphics.Color.parseColor(r1);
        songLyrics.setTextColor(r01);
        webView.loadUrl("https://interutocm.herokuapp.com");
        songLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://interutocm.herokuapp.com");
                String r1 = "#EAEDF1";
                songLyrics.setBackgroundColor(n2);
                projectComments.setBackgroundColor(color);
                String r0 = "#99000000";
                int r01 = android.graphics.Color.parseColor(r0);
                songLyrics.setTextColor(r01);
                projectComments.setTextColor(n1);
                webView.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.VISIBLE);
                writeComment.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        projectComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentModels = new ArrayList<>();
                prepare();
                CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),commentModels);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(commentAdapter);
                //String clo = "#99000000";
                int n = Color.parseColor(r0);
                projectComments.setTextColor(n);

                songLyrics.setTextColor(n1);
                projectComments.setBackgroundColor(n2);
                songLyrics.setBackgroundColor(color);
                recyclerView.setVisibility(View.VISIBLE);
                writeComment.setVisibility(View.VISIBLE);
                actionButton.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
            }
        });

        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDialog commentDialog = new CommentDialog();
                commentDialog.showDialog(SongDetail.this);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        if(intent != null){
            String sessionId = intent.getStringExtra("title");
            projectTitle.setText(sessionId);
        }
        else{
            Toast.makeText(SongDetail.this, "Intent is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepare() {
        commentModels.add(new CommentModel( "Kemoi Kibet", "Wow! This is a nice song. Cant wait for the next"));
        commentModels.add(new CommentModel( "Inoc Prof", "Fire oh Maat!"));
        commentModels.add(new CommentModel( "Hillary Mutai", "Icherun Ake ooh makatani"));

    }

}