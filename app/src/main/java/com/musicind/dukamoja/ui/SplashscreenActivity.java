package com.musicind.dukamoja.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicind.dukamoja.R;

public class SplashscreenActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView app_name;
    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        imageView = findViewById(R.id.imageView2);
        app_name = findViewById(R.id.app_name);
        slogan = findViewById(R.id.slogan);

        imageView.setAnimation(topAnim);
        app_name.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        int DELAY_TIME = 4000;
        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashscreenActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }, DELAY_TIME);
    }
}