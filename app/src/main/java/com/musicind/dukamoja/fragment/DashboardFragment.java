package com.musicind.dukamoja.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.MediaRecyclerAdapter;
import com.musicind.dukamoja.async.CustomItemClickListener;
import com.musicind.dukamoja.async.DownloadBinder;
import com.musicind.dukamoja.async.DownloadManager;
import com.musicind.dukamoja.async.DownloadService;
import com.musicind.dukamoja.async.DownloadUtil;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.response.JSONResponse;
import com.musicind.dukamoja.models.MediaItem;
import com.musicind.dukamoja.ui.HomeActivity;
import com.musicind.dukamoja.ui.IMainActivity;
import com.musicind.dukamoja.ui.LoginActivity;
import com.musicind.dukamoja.util.TokenManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.musicind.dukamoja.util.Constants.KEY_SELECTED_SUBJECT;

public class DashboardFragment extends DialogFragment implements MediaRecyclerAdapter.OnItemClickListener, CustomItemClickListener {

    float v=0;
    private static final String TAG = "DashboardFragment";

    //views
    private View view;
    private IMainActivity iMainActivity;
    private RecyclerView recyclerView;
    private TextView offlineTextView;

    //vars
    private MediaRecyclerAdapter adapter;
    DownloadManager downloadManager;
    private List<MediaMetadataCompat> mediaList;
    private String selectedSubject;
    private MediaMetadataCompat selectedMedia;
    //private List<String> testingGson = new ArrayList<>();
    private List<MediaItem> cachedMediaItems;
    //added variable

    private ArrayList<MediaItem> data;
    TokenManager tokenManager;
    RequestInterface request;
    Call<JSONResponse> call;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //async task functions
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    /*<<<<<<<<<< Start of download Variables >>>>>>>>>>>>>>>*/
    CustomItemClickListener clickListener;
    private DownloadBinder downloadBinder = null;
    private int REQUEST_WRITE_PERMISSION_CODE = 1;
    private Handler updateButtonStateHandler = null;
    private int MESSAGE_UPDATE_START_BUTTON = 2;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadBinder)service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    /*<<<<<<<<<< End of download Variables >>>>>>>>>>>>>>>*/

    public static DashboardFragment newInstance(String subject) {
        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SELECTED_SUBJECT, subject);
        dashboardFragment.setArguments(bundle);
        return dashboardFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = requireActivity().getSharedPreferences("", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
        tokenManager = TokenManager.getInstance(requireActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        }
        request = RetrofitBuilder.createServiceWithAuth(RequestInterface.class, tokenManager);
        view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        cachedMediaItems = new ArrayList<>();
        if (getArguments() != null) {
            selectedSubject = getArguments().getString(KEY_SELECTED_SUBJECT);
            iMainActivity.getToolBar().setTitle(selectedSubject);
            Log.d(TAG, "onCreateView: selected subjects is :" + selectedSubject);
        }
        initRecyclerView();
        /*<<<<<<<<<< Start of download functions >>>>>>>>>>>>>>>*/
        startAndBindDownloadService();
        //initControls();
        /*<<<<<<<<<< End of download functions >>>>>>>>>>>>>>>*/
        if (savedInstanceState != null) {
            adapter.setSelectedIndex(savedInstanceState.getInt("selected_index"));
        }

        return view;

    }

    private void startAndBindDownloadService() {
        Intent downloadIntent = new Intent(getActivity(), DownloadService.class);
        requireActivity().startService(downloadIntent);
        requireActivity().bindService(downloadIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_index", adapter.getSelectedIndex());
    }

    private void initRecyclerView() {
        mediaList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.idCourseRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO set the adapter after filling the list
        adapter = new MediaRecyclerAdapter(mediaList, getActivity(), clickListener);
        adapter.setOnItemClickListener(this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if (mediaList.size() == 0 && isConnected()) {
            retrieveDate();
//            Log.d(TAG, "initRecyclerView: saved list is : "+ iMainActivity.getMyPreferenceManager().getOperativeRecords().get(0).getTitle());
//            Log.d(TAG, "initRecyclerView: saved list is : "+ iMainActivity.getMyPreferenceManager().getCrownRecords().get(0).getTitle());
        } else {
            if (iMainActivity.getMyPreferenceManager().getCrownRecords() == null) {
                offlineTextView.setVisibility(View.VISIBLE);
            } else {
//                Log.d(TAG, "initRecyclerView: saved list is : "+ iMainActivity.getMyPreferenceManager().getOperativeRecords().get(0).getTitle());
//                Log.d(TAG, "initRecyclerView: saved list is : "+ iMainActivity.getMyPreferenceManager().getCrownRecords().get(0).getTitle());
                if(!mediaList.isEmpty()){
                    mediaList.clear();
                }
                Toast.makeText(getActivity(), "retrieving cached data", Toast.LENGTH_SHORT).show();
                retrieveCachesData();
            }

        }
    }

//    private void convertToCashedList(List<MediaMetadataCompat> mediaList) {
//        int i = 0;
//        cachedMediaItems.clear();
//        for (MediaMetadataCompat mediaItem : mediaList) {
//            cachedMediaItems.add(new MediaItem(
//                    mediaItem.getDescription().getMediaId(),
//                    mediaItem.getDescription().getTitle().toString(),
//                    mediaItem.getDescription().getSubtitle().toString(),
//                    mediaItem.getDescription().getMediaUri().toString()
//            ));
//            Log.d(TAG, "convertToCashedList: caches items is :" + cachedMediaItems.get(i).getId());
//            Log.d(TAG, "convertToCashedList: caches items is :" + cachedMediaItems.get(i).getTitle());
//            Log.d(TAG, "convertToCashedList: caches items is :" + cachedMediaItems.get(i).getDescription());
//            Log.d(TAG, "convertToCashedList: caches items is :" + cachedMediaItems.get(i++).getMediaUrl());
//        }
//        cacheRecords();
//        Toast.makeText(getActivity(), "media list saved", Toast.LENGTH_LONG).show();
//        Log.d(TAG, "convertToCashedList: the cached list is :" + retrieveCachedRecords());
//    }

    private void retrieveCachesData() {
        if(!mediaList.isEmpty()){
            mediaList.clear();
        }
        Log.d(TAG, "retrieveCachesData: selected subject is " + selectedSubject);
        Log.d(TAG, "retrieveCachesData: list is : " + retrieveCachedRecords().get(0).getTitle());
        for (MediaItem mediaItem : retrieveCachedRecords()) {
            MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaItem.getId())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaItem.getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaItem.getDescription())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaItem.getMediaUrl())
                    .build();

            mediaList.add(mediaMetadataCompat);
        }
    }

    private void cacheRecords() {
        Log.d(TAG, "cacheRecords: beeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeen HERE!!!");
        Toast.makeText(getActivity(), "Been here", Toast.LENGTH_LONG).show();
        switch (selectedSubject) {
            case "Crown":
                Toast.makeText(getActivity(), "Cached to Crown", Toast.LENGTH_SHORT).show();
                iMainActivity.getMyPreferenceManager().saveCrownRecords(this.cachedMediaItems);
                Log.d(TAG, "cacheRecords: records     " + iMainActivity.getMyPreferenceManager().getCrownRecords().get(0).getTitle());
                return;
            case "Operative":
                Toast.makeText(getActivity(), "Cached to operative", Toast.LENGTH_SHORT).show();
                iMainActivity.getMyPreferenceManager().saveOperativeRecords(this.cachedMediaItems);
                Log.d(TAG, "cacheRecords: records     " + iMainActivity.getMyPreferenceManager().getOperativeRecords().get(0).getTitle());
                return;
            case "Oral Pathology":
                iMainActivity.getMyPreferenceManager().saveOralRecords(this.cachedMediaItems);
                return;
            case "Ortho":
                iMainActivity.getMyPreferenceManager().saveOrthoRecords(this.cachedMediaItems);
                return;
            case "Pedo":
                iMainActivity.getMyPreferenceManager().savePedoRecords(this.cachedMediaItems);
                return;
            case "Pharma":
                iMainActivity.getMyPreferenceManager().savePharmaRecords(this.cachedMediaItems);
                return;
            case "Prosthesis":
                iMainActivity.getMyPreferenceManager().saveProsthesisRecords(this.cachedMediaItems);
                return;
        }
    }

    private List<MediaItem> retrieveCachedRecords() {
        Log.d(TAG, "retrieveCachedRecords: selected subject" + selectedSubject);
        switch (selectedSubject) {
            case "Crown":
                Log.d(TAG, "retrieveCachedRecords: " + iMainActivity.getMyPreferenceManager().getCrownRecords().get(0));
                return iMainActivity.getMyPreferenceManager().getCrownRecords();
            case "Operative":
                Log.d(TAG, "retrieveCachedRecords: " + iMainActivity.getMyPreferenceManager().getOperativeRecords().get(0));
                return iMainActivity.getMyPreferenceManager().getOperativeRecords();
            case "Oral Pathology":
                return iMainActivity.getMyPreferenceManager().getOralRecords();
            case "Ortho":
                return iMainActivity.getMyPreferenceManager().getOrthoRecords();
            case "Pedo":
                return iMainActivity.getMyPreferenceManager().getPedoRecords();
            case "Pharma":
                return iMainActivity.getMyPreferenceManager().getPharmaRecords();
            case "Prosthesis":
                return iMainActivity.getMyPreferenceManager().getProsthesisRecords();
            default:
                return iMainActivity.getMyPreferenceManager().getCrownRecords();
        }
    }

    private void retrieveDate(){
        iMainActivity.showProgress();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://interutocm.herokuapp.com/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
        call = request.getJSON();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    JSONResponse jsonResponse = response.body();
                    assert jsonResponse != null;
                    System.out.println(jsonResponse.toString());
                    data = new ArrayList<>(Arrays.asList(jsonResponse.getAndroid()));

                    addToMediaList(data);
                    //adapter = new DataAdapter(data, MainActivity.this);
                    System.out.println(jsonResponse.toString());
                    updateDataSet();
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                }

            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }


    private void addToMediaList(ArrayList<MediaItem> mediaData) {
        for(int i=0; i<data.size(); i++) {
            MediaMetadataCompat media = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mediaData.get(i).getFile()))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaData.get(i).getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaData.get(i).getDescription())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaData.get(i).getMediaUrl())
                    .build();
            mediaList.add(media);
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void getSelectedMediaItem(String mediaId) {
        for (MediaMetadataCompat media : mediaList) {
            if (media.getDescription().getMediaId().equals(mediaId)) {
                selectedMedia = media;
                adapter.setSelectedIndex(adapter.getIndexOfItem(selectedMedia));
                break;
            }
        }
    }

    private void updateDataSet() {
      iMainActivity.hideProgress();
        adapter.notifyDataSetChanged();

        if (iMainActivity.getMyPreferenceManager().getLastSubject().equals(selectedSubject)) {
            getSelectedMediaItem(iMainActivity.getMyPreferenceManager().getLastPlayedMedia());
        }
    }

    public void updateUI(MediaMetadataCompat mediaItem) {
        adapter.setSelectedIndex(adapter.getIndexOfItem(mediaItem));
        selectedMedia = mediaItem;
        saveLastPlayedSongProperties();
    }
    @Override
    public void onItemClick(String data) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "This app need write sdcard permission, please allow.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION_CODE);
        } else {
            downloadBinder.startDownload(data, 0);
            // startDownloadButton.setEnabled(false);
            Thread enableButtonThread = new Thread()
            {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (downloadBinder.getDownloadManager().isDownloadCanceled()) {
                                Message msg = new Message();
                                msg.what = MESSAGE_UPDATE_START_BUTTON;
                                updateButtonStateHandler.sendMessage(msg);
                                break;
                            }
                            Thread.sleep(2000);
                        }catch(Exception ex)
                        {
                            Log.e(DownloadUtil.TAG_DOWNLOAD_MANAGER, ex.getMessage(), ex);
                        }
                    }
                }
            };
            enableButtonThread.start();
        }
    }

    @Override
    public void onShowToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        downloadBinder.cancelDownload();
    }

    @Override
    public void onItemClick(int position) {
        adapter.setSelectedIndex(position);
        iMainActivity.getMyApplication().setMediaItems(mediaList);
        selectedMedia = mediaList.get(position);
        iMainActivity.onMediaSelected(selectedSubject, selectedMedia, position);
        saveLastPlayedSongProperties();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iMainActivity = (HomeActivity) getActivity();
    }

    private void saveLastPlayedSongProperties() {
        iMainActivity.getMyPreferenceManager().setPlaylistId(selectedSubject);
        iMainActivity.getMyPreferenceManager().saveLastPlayedMedia(selectedMedia.getDescription().getMediaId());
        iMainActivity.getMyPreferenceManager().saveLastPlayedSubject(selectedSubject);
        //TODO see if there is anything else to be saved in the shared preferences
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unbindService(serviceConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_WRITE_PERMISSION_CODE)
        {
            int grantResult = grantResults[0];
            if(grantResult == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(requireActivity(), "You can continue to use this app.", Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(requireActivity(), "You disallow write external storage permission, app closed.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }


}
