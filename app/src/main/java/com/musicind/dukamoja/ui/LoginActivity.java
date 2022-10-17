package com.musicind.dukamoja.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;

import android.os.Bundle;


import com.facebook.CallbackManager;
import com.google.android.material.tabs.TabLayout;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.LoginAdapter;
import com.musicind.dukamoja.models.AccessToken;
import com.musicind.dukamoja.util.FacebookManager;

public class LoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    float v=0;
    private CallbackManager callbackManager;
    FacebookManager facebookManager;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter loginAdapter = new LoginAdapter(getSupportFragmentManager(),this,tabLayout.getTabCount());

        viewPager.setAdapter(loginAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

       // fb.setTranslationY(300);
//        twitter.setTranslationY(300);
        tabLayout.setTranslationY(300);

      //  fb.setAlpha(v);
//        twitter.setAlpha(v);
        tabLayout.setAlpha(v);

      //  fb.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
//        twitter.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    }
