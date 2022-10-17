package com.musicind.dukamoja.ui;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.musicind.dukamoja.BuildConfig;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.CommentAdapter;
import com.musicind.dukamoja.adapter.DrawerAdapter;
import com.musicind.dukamoja.client.MediaBrowserHelper;
import com.musicind.dukamoja.client.MediaBrowserHelperCallback;
import com.musicind.dukamoja.dialog.CommentDialog;
import com.musicind.dukamoja.fragment.DashboardFragment;
import com.musicind.dukamoja.fragment.VideosFragment;
import com.musicind.dukamoja.fragment.FanpageFragment;
import com.musicind.dukamoja.fragment.ProfileFragment;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.models.CommentModel;
import com.musicind.dukamoja.response.CommentResponse;
import com.musicind.dukamoja.models.DrawerItem;
import com.musicind.dukamoja.response.JSONResponse;
import com.musicind.dukamoja.models.MediaItem;
import com.musicind.dukamoja.models.SimpleItem;
import com.musicind.dukamoja.models.SpaceItem;
import com.musicind.dukamoja.services.MediaDownloadService;
import com.musicind.dukamoja.services.MediaService;
import com.musicind.dukamoja.util.MediaSeekBar;
import com.musicind.dukamoja.util.MyApplication;
import com.musicind.dukamoja.util.MyPreferenceManager;
import com.musicind.dukamoja.util.TokenManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.musicind.dukamoja.util.Constants.CUSTOM_ACTION_SPEED;
import static com.musicind.dukamoja.util.Constants.DECREASE_15;
import static com.musicind.dukamoja.util.Constants.INCREASE_15;
import static com.musicind.dukamoja.util.Constants.MEDIA_QUEUE_POSITION;
import static com.musicind.dukamoja.util.Constants.QUEUE_NEW_PLAYLIST;
import static com.musicind.dukamoja.util.Constants.SEEK_BAR_MAX;
import static com.musicind.dukamoja.util.Constants.SEEK_BAR_PROGRESS;
import static com.musicind.dukamoja.util.Constants.SPEED_VALUE;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, IMainActivity, MediaBrowserHelperCallback, View.OnClickListener {

    float v=0;

    private static final String TAG = "HomeActivity";
    public static boolean firstTime = false;

    //Home activity variables
    private static final int POS_CLOSE = 0;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_MY_PROFILE = 2;
    private static final int POS_EVENTS = 3;
    private static final int POS_FAN_PAGE = 4;
    private static final int POS_SHARE = 5;
    private static final int POS_LOGOUT = 7;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootnav;

    //UI components
    private ProgressBar progressBar;
    private androidx.appcompat.widget.Toolbar toolbar;
    private RelativeLayout bottomMediaControllerLayout
            , fullPanelLayout;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageView playPauseImage,
            fullPanelPlayPause,
            fullPanelSkipToNext,
            fullPanelSkipToPrevious;
    private TextView titleTextView,
            fullPanelTextTitle,
            fullPanelTextDescription;
    private MediaSeekBar fullPanelSeekBar;
    private ProgressBar horizontalProgressBar;
    private ImageView
            skip15ToPrevious,
            skip15ToNext,
            downloadMediaImage;
    private ImageView speedImageView;
    private Chronometer trackingChronometer;
    private Chronometer fullChronometer;
    private WebView webView;
    private RecyclerView recyclerView;
    private TextView projectComments, songLyrics;
    private FloatingActionButton writeComment;
    private AppCompatButton actionButton;

    //slider components


    private LottieAnimationView downloadAnimation;

    //Vars
    private MediaBrowserHelper mediaBrowserHelper;
    private boolean isPlaying;
    private MyApplication myApplication;
    private MyPreferenceManager myPreferenceManager;
    private SeekBarBroadCastReceiver seekBarBroadCastReceiver;
    private UpdateUIBroadCastReceiver updateUIBroadCastReceiver;
    private MediaMetadataCompat selectedMedia;
    private boolean onAppOpen;
    private boolean wasConfigurationChanged;
    private ArrayList<MediaItem> data;
    private ArrayList<CommentModel> commentModels;
    private long seekBarProgress;

    RequestInterface request;
    Call<JSONResponse> call;
    TokenManager tokenManager;
    Call<CommentResponse> commentResponseCall;
    CommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI(savedInstanceState);
        firstTime = true;
        if (savedInstanceState == null) {
            loadMediaBaseFragment(DashboardFragment.newInstance("All Music"));
        } else {
            selectedMedia = savedInstanceState.getParcelable("selected_media");
            isPlaying = savedInstanceState.getBoolean("is_playing");
            if (selectedMedia != null) {
                titleTextView.setText(selectedMedia.getDescription().getTitle());
                setIsPlaying(isPlaying);
            }
        }

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
        request = RetrofitBuilder.createServiceWithAuth(RequestInterface.class, tokenManager);

        myPreferenceManager = MyPreferenceManager.getInstance(this);
        myPreferenceManager.saveFirstLaunch(true);
        myApplication = new MyApplication();
        mediaBrowserHelper = new MediaBrowserHelper(this, MediaService.class);
        mediaBrowserHelper.setMediaBrowserHelperCallback(this);
        Log.d(TAG, "onCreate: saved playlist id is :" + myPreferenceManager.getPlaylistId());
        //initSpinner();


        //slider -- initial code for the oncreate method


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("selected_media", selectedMedia);
        outState.putBoolean("is_playing", isPlaying);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        wasConfigurationChanged = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: CALLED");
        if (firstTime) {
            if (!getMyPreferenceManager().getPlaylistId().equals("")) {
                prepareLastPlayedMedia();
            } else {
                if (myPreferenceManager.isFirstLaunch()) {
                    mediaBrowserHelper.onStart(wasConfigurationChanged, myPreferenceManager.isFirstLaunch());
                    //initSpinner();
                }
            }
        }
        setIsPlaying(isPlaying);
    }

    private void prepareLastPlayedMedia() {

        final List<MediaMetadataCompat> mediaList = new ArrayList<>();
        /*onFinishedGettingPreviousSessionData(mediaList);*/
        call = request.getJSON();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    JSONResponse jsonResponse = response.body();
                    data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
                    for (int i =0; i<data.size(); i++) {
                        MediaMetadataCompat media = toMediaItem(data, i);
                        mediaList.add(media);
                        if (media.getDescription().getMediaId().equals(myPreferenceManager.getLastPlayedMedia())) {
                            titleTextView.setText(media.getDescription().getTitle());
                            fullPanelTextTitle.setText(media.getDescription().getTitle());
                            fullPanelTextDescription.setText(media.getDescription().getSubtitle());
                            updateTrackingChronometer();
                            fullPanelSeekBar.setProgress(myPreferenceManager.getSeekBarPosition());
                            //horizontalProgressBar.setProgress(myPreferenceManager.getSeekBarPosition());
                        }
                    }
                    onFinishedGettingPreviousSessionData(mediaList);
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

    }

    private MediaMetadataCompat toMediaItem(ArrayList<MediaItem> mediaData, int i) {

      //  for(int i=0; i<data.size(); i++) {
        MediaMetadataCompat media = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mediaData.get(i).getFile()))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaData.get(i).getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaData.get(i).getDescription())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaData.get(i).getMediaUrl())
                    .build();
      //  }
        return media;
    }

    private void onFinishedGettingPreviousSessionData(List<MediaMetadataCompat> mediaList) {
        myApplication.setMediaItems(mediaList);
        mediaBrowserHelper.onStart(wasConfigurationChanged, myPreferenceManager.isFirstLaunch());
    }

//    private MediaMetadataCompat toMediaItem(QueryDocumentSnapshot document) {
//        MediaMetadataCompat media = new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, document.getId())
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, document.get("title").toString())
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, document.get("description").toString())
//                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, document.get("media_url").toString())
//                .build();
//        return media;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        initSeekBarBroadCast();
        initUpdateUIBroadCast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: CALLED");
        if (!getMyPreferenceManager().getPlaylistId().equals("")) {
            prepareLastPlayedMedia();
        } else {
            if (myPreferenceManager.isFirstLaunch()) {
                mediaBrowserHelper.onStart(wasConfigurationChanged, myPreferenceManager.isFirstLaunch());
                //initSpinner();
            }
        }
        Log.d(TAG, "onStart: CALLED");
        setIsPlaying(isPlaying);
        firstTime = false;
        if (seekBarBroadCastReceiver != null) {
            unregisterReceiver(seekBarBroadCastReceiver);
        }
        if (updateUIBroadCastReceiver != null) {
            unregisterReceiver(updateUIBroadCastReceiver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: CALLED");
        myPreferenceManager.saveFirstLaunch(false);
        firstTime = false;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void playPause() {

    }
    @Override
    public void onMediaControllerConnected(MediaControllerCompat mediaController) {
        //seekBar.setEnabled(false);
        //seekBar.setMediaController(mediaController);
        fullPanelSeekBar.setMediaController(mediaController);
    }

    @Override
    public MyApplication getMyApplication() {
        return myApplication;
    }

    @Override
    public MyPreferenceManager getMyPreferenceManager() {
        return myPreferenceManager;
    }

    @Override
    public void onMediaSelected(String playlistId, MediaMetadataCompat mediaItem, int queuePosition) {
        if (mediaItem != null) {
            Log.d(TAG, "onMediaSelected: Called :" + mediaItem.getDescription().getTitle());

            String currentPlaylistId = myPreferenceManager.getPlaylistId();

            Bundle bundle = new Bundle();
            bundle.putInt(MEDIA_QUEUE_POSITION, queuePosition);
            if (currentPlaylistId.equals(playlistId) && !firstTime) {
                mediaBrowserHelper.getTransportControls().playFromMediaId(mediaItem.getDescription().getMediaId(), bundle);
            } else {
                bundle.putBoolean(QUEUE_NEW_PLAYLIST, true);
                Log.d(TAG, "onMediaSelected: playlist id is :" + playlistId);
                mediaBrowserHelper.subscribeToNewPlaylist(playlistId);
                mediaBrowserHelper.getTransportControls().playFromMediaId(mediaItem.getDescription().getMediaId(), bundle);
                firstTime = false;
            }
            //because this means that the user has selected something to play
            onAppOpen = true;
        } else {
            Toast.makeText(this, "selected something to play", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public androidx.appcompat.widget.Toolbar getToolBar() {
        return toolbar;
    }


    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: CALLED");
        isPlaying = (state != null) && (state.getState() == PlaybackStateCompat.STATE_PLAYING);

        setIsPlaying(isPlaying);
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metaData) {
        selectedMedia = metaData;
        titleTextView.setText(metaData.getDescription().getTitle());
        fullPanelTextTitle.setText(metaData.getDescription().getTitle());
        fullPanelTextDescription.setText(metaData.getDescription().getSubtitle());
        fullChronometer.setBase(myPreferenceManager.getChronometerDuration());
    }

    public void setIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_pause)
                    .into(playPauseImage);
            Glide.with(getApplicationContext())
                    .load(R.drawable.icons8_pause_64)
                    .into(fullPanelPlayPause);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_play)
                    .into(playPauseImage);
            Glide.with(getApplicationContext())
                    .load(R.drawable.icons8_play_64)
                    .into(fullPanelPlayPause);
        }
        this.isPlaying = isPlaying;
    }

    //TODO check this if there is an error
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myPreferenceManager.setPlaylistId(null);
        fullPanelSeekBar.disconnectController();
        mediaBrowserHelper.onStop();
        Log.d(TAG, "onDestroy: Called  " + myPreferenceManager.getPlaylistId());
    }

    private void updateTrackingChronometer() {
        fullChronometer.setBase(myPreferenceManager.getChronometerDuration());
    }

    private class SeekBarBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            seekBarProgress = intent.getLongExtra(SEEK_BAR_PROGRESS, 0);
            long max = intent.getLongExtra(SEEK_BAR_MAX, 0);
            myPreferenceManager.saveChronometerDuration(SystemClock.elapsedRealtime() - max);
            if (!fullPanelSeekBar.isTracking()) {
                horizontalProgressBar.setProgress((int) seekBarProgress);
                horizontalProgressBar.setMax((int) max);
                fullPanelSeekBar.setProgress((int) seekBarProgress);
                fullPanelSeekBar.setMax((int) max);
                myPreferenceManager.saveSeekBarPosition((int) seekBarProgress);
                fullChronometer.setBase(SystemClock.elapsedRealtime() - max);
            }
            trackingChronometer.setBase(SystemClock.elapsedRealtime() - seekBarProgress);
            // myPreferenceManager.saveSeekBarPosition((int)progress);
            //Log.d(TAG, "onReceive: seek bar position" + myPreferenceManager.getSeekBarPosition());
        }
    }

    private class UpdateUIBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String newMediaId = intent.getStringExtra(getString(R.string.broadcast_new_media_id));
            Log.d(TAG, "onReceive: CALLED  " + newMediaId);
            if (getMediaBaseFragment() != null) {
                Log.d(TAG, "onReceive: " + myApplication.getMediaItem(newMediaId).getDescription().getMediaId());
                getMediaBaseFragment().updateUI(myApplication.getMediaItem(newMediaId));
            }
        }
    }

    private void initUpdateUIBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_update_ui));
        updateUIBroadCastReceiver = new UpdateUIBroadCastReceiver();
        registerReceiver(updateUIBroadCastReceiver, intentFilter);
    }

    private void initSeekBarBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.broadcast_seekbar_update));
        seekBarBroadCastReceiver = new SeekBarBroadCastReceiver();
        registerReceiver(seekBarBroadCastReceiver, intentFilter);
    }

    private void downloadMedia() {
        downloadAnimation.playAnimation();
        downloadAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                downloadAnimation.setProgress(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        ProgressiveDownloadAction progressiveDownloadAction = new ProgressiveDownloadAction(selectedMedia.getDescription().getMediaUri(),
                false, null, null);
        DownloadService.startWithAction(this, MediaDownloadService.class, progressiveDownloadAction, false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_pause_image_view:
            case R.id.play_pause_full_panel:
                playPauseToggle();
                return;
            case R.id.full_panel_skip_to_next:
                mediaBrowserHelper.getTransportControls().skipToNext();
                return;
            case R.id.full_panel_skip_to_previous:
                mediaBrowserHelper.getTransportControls().skipToPrevious();
                return;
            case R.id.skip_15_to_next_image:
                mediaBrowserHelper.getTransportControls().sendCustomAction(INCREASE_15, new Bundle());
                return;
            case R.id.skip_15_to_previous_image:
                mediaBrowserHelper.getTransportControls().sendCustomAction(DECREASE_15, new Bundle());

                return;
            case R.id.download_could_animation:
                downloadMedia();
                return;
            case R.id.speed_image_view:
                initPopupMenu(view);
        }
    }

    private void initPopupMenu(View view) {
        final Bundle bundle = new Bundle();
        final PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.getMenu().getItem(myPreferenceManager.getSpeedIndex()).setChecked(true);
        Log.d(TAG, "initPopupMenu: speed index is :" + myPreferenceManager.getSpeedIndex());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(HomeActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                //popupMenu.getMenu().getItem(1).setChecked(false);
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.speed__75:
                        myPreferenceManager.setPlaybackSpeedIndex(0);
                        bundle.putFloat(SPEED_VALUE, .75f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;
                    case R.id.speed_1:
                        myPreferenceManager.setPlaybackSpeedIndex(1);
                        bundle.putFloat(SPEED_VALUE, 1.0f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;
                    case R.id.speed_1_25:
                        myPreferenceManager.setPlaybackSpeedIndex(2);
                        bundle.putFloat(SPEED_VALUE, 1.25f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;
                    case R.id.speed_1_5:
                        myPreferenceManager.setPlaybackSpeedIndex(3);
                        bundle.putFloat(SPEED_VALUE, 1.5f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;
                    case R.id.speed_2:
                        myPreferenceManager.setPlaybackSpeedIndex(4);
                        bundle.putFloat(SPEED_VALUE, 2.0f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;
                    case R.id.speed_3:
                        myPreferenceManager.setPlaybackSpeedIndex(5);
                        bundle.putFloat(SPEED_VALUE, 3.0f);
                        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
                        break;

                }
                //myPreferenceManager.setPlaybackSpeedIndex(menuItem.getOrder());
                return true;
            }
        });
        //DON"T forget this
        popupMenu.show();
        Log.d(TAG, "initPopupMenu: speed index is :" + myPreferenceManager.getSpeedIndex());
    }

    private void playPauseToggle() {

        // mediaBrowserHelper.getTransportControls().play();
        Log.d(TAG, "playPause: onAppOpen is :" + onAppOpen);
        if (onAppOpen) {
            if (isPlaying) {
                mediaBrowserHelper.getTransportControls().pause();
            } else {
                mediaBrowserHelper.getTransportControls().play();
                speedMediaUp();
                isPlaying = false;
            }
        } else {
            if (!myPreferenceManager.getPlaylistId().equals("")) {
                onMediaSelected(myPreferenceManager.getPlaylistId(),
                        myApplication.getMediaItem(myPreferenceManager.getLastPlayedMedia()),
                        myPreferenceManager.getQueuePosition());
                //seekBar.setProgress(myPreferenceManager.getSeekBarPosition());
                speedMediaUp();
                Toast.makeText(this, "the index is :" + myPreferenceManager.getQueuePosition(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Select something to play", Toast.LENGTH_SHORT).show();
            }
            isPlaying = true;
        }
    }

    private void speedMediaUp() {
        Bundle bundle = new Bundle();
        bundle.putFloat(SPEED_VALUE, getSavedSpeed());
        Toast.makeText(this, "speed is " + getSavedSpeed(), Toast.LENGTH_SHORT).show();
        mediaBrowserHelper.getTransportControls().sendCustomAction(CUSTOM_ACTION_SPEED, bundle);
    }

    private Float getSavedSpeed() {
        switch (myPreferenceManager.getSpeedIndex()) {
            case 0:
                return .75f;
            case 1:
                return 1.0f;
            case 2:
                return 1.25f;
            case 3:
                return 1.5f;
            case 4:
                return 2.0f;
            case 5:
                return 3.0f;
            default:
                return 1.0f;
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            //finish();
            finishAffinity();
        }

    }

    private DashboardFragment getMediaBaseFragment() {
        DashboardFragment dashboardFragment = (DashboardFragment) getSupportFragmentManager()
                .findFragmentByTag("DashboardFragment");
        if (dashboardFragment != null) {
            return dashboardFragment;
        }
        return null;
    }

    private void loadMediaBaseFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, "DashboardFragment")
                .commit();
    }

    private void initUI(Bundle savedInstanceState) {
        fullChronometer = findViewById(R.id.chronometer_full_duration);
        trackingChronometer = findViewById(R.id.chronometer_current_position);
        speedImageView = findViewById(R.id.speed_image_view);
        speedImageView.setOnClickListener(this);
        downloadAnimation = findViewById(R.id.download_could_animation);
        downloadAnimation.setOnClickListener(this);
        //downloadMediaImage = findViewById(R.id.download_media_image);
//        downloadMediaImage.setOnClickListener(this);
        skip15ToNext = findViewById(R.id.skip_15_to_next_image);
        skip15ToPrevious = findViewById(R.id.skip_15_to_previous_image);
        skip15ToPrevious.setOnClickListener(this);
        skip15ToNext.setOnClickListener(this);
        horizontalProgressBar = findViewById(R.id.horizontal_progress_bar);
        fullPanelSeekBar = findViewById(R.id.full_panel_seek_bar);
        //seekBar = findViewById(R.id.bottom_panel_seek_bar);
        fullPanelSkipToPrevious = findViewById(R.id.full_panel_skip_to_previous);
        fullPanelSkipToNext = findViewById(R.id.full_panel_skip_to_next);
        fullPanelSkipToNext.setOnClickListener(this);
        fullPanelSkipToPrevious.setOnClickListener(this);
        fullPanelPlayPause = findViewById(R.id.play_pause_full_panel);
        fullPanelPlayPause.setOnClickListener(this);
        fullPanelTextTitle = findViewById(R.id.full_panel_text_title);
        fullPanelTextDescription = findViewById(R.id.full_panel_text_description);
        playPauseImage = findViewById(R.id.play_pause_image_view);
        playPauseImage.setOnClickListener(this);
        titleTextView = findViewById(R.id.media_song_title_text_view);
        fullPanelLayout = findViewById(R.id.full_panel);
        bottomMediaControllerLayout = findViewById(R.id.bottom_panel);
        slidingUpPanelLayout = findViewById(R.id.main_sliding_up_panel);
        progressBar = findViewById(R.id.progress_bar);
        toolbar = findViewById(R.id.main_tool_bar);

        webView = findViewById(R.id.projectDetailsWebView);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        projectComments = findViewById(R.id.songComments);
        songLyrics = findViewById(R.id.songLyrics);
        writeComment = findViewById(R.id.writeComment);
        actionButton = findViewById(R.id.actionButton);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                bottomMediaControllerLayout.setAlpha(1 - slideOffset);
                fullPanelLayout.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    bottomMediaControllerLayout.setVisibility(View.GONE);
                } else {
                    bottomMediaControllerLayout.setVisibility(View.VISIBLE);
                }
            }
        });

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
                initRecyclerView();
               // commentModels = new ArrayList<>();
             //   initRecyclerView();
//                CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),commentModels);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                recyclerView.setLayoutManager(linearLayoutManager);
//                recyclerView.setAdapter(commentAdapter);
                //String clo = "#99000000";
//                int n = Color.parseColor(r0);
//                projectComments.setTextColor(n);
//
//                songLyrics.setTextColor(n1);
//                projectComments.setBackgroundColor(n2);
//                songLyrics.setBackgroundColor(color);
//                recyclerView.setVisibility(View.VISIBLE);
//                writeComment.setVisibility(View.VISIBLE);
//                actionButton.setVisibility(View.GONE);
//                webView.setVisibility(View.GONE);
            }
        });

        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDialog commentDialog = new CommentDialog();
                commentDialog.showDialog(HomeActivity.this);
            }
        });

        //main home activity content
//        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_tool_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);


        slidingRootnav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter1 = new DrawerAdapter(Arrays.asList(
                createItemfor(POS_CLOSE),
                createItemfor(POS_DASHBOARD).setIsChecked(true),
                createItemfor(POS_MY_PROFILE),
                createItemfor(POS_EVENTS),
                createItemfor(POS_FAN_PAGE),
                createItemfor(POS_SHARE),
                new SpaceItem(150),
                createItemfor(POS_LOGOUT)

        ));
        adapter1.setListener(this);
        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter1);

        adapter1.setSelected(POS_DASHBOARD);

        if(createItemfor(POS_DASHBOARD).isChecked()){
            setSupportActionBar(toolbar);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.black)));
        }

        //slidingUpPanelLayout.setDragView(R.id.bottom_panel);
        //initSpinner();
    }

    int readLastButtonPressed(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("application", Context.MODE_PRIVATE);
        return sharedPref.getInt("SONG_ID", 0);
    }

    void getComments(Activity activity){
        commentResponseCall = request.getComments(readLastButtonPressed(activity));
        commentResponseCall.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    CommentResponse commentResponse = response.body();
                    assert commentResponse != null;
                    ArrayList<CommentModel> comment = new ArrayList<>(commentResponse.getComments());
                    commentModels.addAll(comment);
                    assert response.body() != null;
                    //Log.w(TAG, "onResponse: " + comment.get(0).getUserName() );
                    //data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));
                   // Toast.makeText(activity, "genius" +comment.get(0).getUserName(), Toast.LENGTH_SHORT).show();
                    updateDataSet();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }
    private void updateDataSet() {
        adapter.notifyDataSetChanged();

    }
    private void initRecyclerView() {
        commentModels = new ArrayList<>();
        String r1 = "#EAEDF1";
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
        //recyclerView = view.findViewById(R.id.idCourseRV);
        adapter = new CommentAdapter(this, commentModels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //TODO set the adapter after filling the list
        recyclerView.setAdapter(adapter);
        //prepare();
        getComments(this);
        int n4 = Color.parseColor(r0);
        projectComments.setTextColor(n4);
        songLyrics.setTextColor(n1);
        projectComments.setBackgroundColor(n2);
        songLyrics.setBackgroundColor(color);
        recyclerView.setVisibility(View.VISIBLE);
        writeComment.setVisibility(View.VISIBLE);
        actionButton.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);

    }


    private void prepare() {
        commentModels.add(new CommentModel( "Kemoi Kibet", "Wow! This is a nice song. Cant wait for the next"));
        commentModels.add(new CommentModel( "Inoc Prof", "Fire oh Maat!"));
        commentModels.add(new CommentModel( "Hillary Mutai", "Icherun Ake ooh makatani"));

    }


    private DrawerItem createItemfor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconInt(color(R.color.pink))
                .withTextInt(color(R.color.black))
                .withSelectedIconInt(color(R.color.pink))
                .withSelectedTextInt(color(R.color.pink));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitle);

    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcon);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i =0; i<ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this,id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (position == POS_DASHBOARD){
            loadMediaBaseFragment(DashboardFragment.newInstance("All Music"));
            //DashboardFragment dashboardFragment = new DashboardFragment();
            //transaction.replace(R.id.container, dashboardFragment);
        }
        else if (position == POS_MY_PROFILE){
            loadMediaBaseFragment(DashboardFragment.newInstance("My Profile"));
            ProfileFragment profileFragment = new ProfileFragment();
            transaction.replace(R.id.container, profileFragment);
        }
        else if (position == POS_EVENTS){
            loadMediaBaseFragment(DashboardFragment.newInstance("My Videos"));
            VideosFragment videosFragment = new VideosFragment();
            transaction.replace(R.id.container, videosFragment);
        }
        else if (position == POS_FAN_PAGE){
            loadMediaBaseFragment(DashboardFragment.newInstance("FAN PAGE"));
            FanpageFragment fanpageFragment = new FanpageFragment();
            transaction.replace(R.id.container, fanpageFragment);
        }
        else if (position == POS_SHARE) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if (position == POS_LOGOUT){
            tokenManager.deleteToken();
            cancelAllNotification(this);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        slidingRootnav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static void cancelAllNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }
}