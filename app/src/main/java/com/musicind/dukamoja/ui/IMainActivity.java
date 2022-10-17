package com.musicind.dukamoja.ui;

import android.support.v4.media.MediaMetadataCompat;

import com.musicind.dukamoja.util.MyApplication;
import com.musicind.dukamoja.util.MyPreferenceManager;

import androidx.appcompat.widget.Toolbar;


public interface IMainActivity {

    void showProgress();
    void hideProgress();
    void playPause();

    MyApplication getMyApplication();

    MyPreferenceManager getMyPreferenceManager();

    void onMediaSelected(String playlistId , MediaMetadataCompat mediaItem,int queuePosition);

    Toolbar getToolBar();
}
