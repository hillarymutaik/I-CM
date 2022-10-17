package com.musicind.dukamoja.services;

import android.app.Notification;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationUtil;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.util.DownloadUtil;

import static com.musicind.dukamoja.util.Constants.DOWNLOAD_CHANNEL_ID;
import static com.musicind.dukamoja.util.Constants.DOWNLOAD_NOTIFICATION_ID;

public class MediaDownloadService extends DownloadService {

    public MediaDownloadService(){
        super(DOWNLOAD_NOTIFICATION_ID,DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_CHANNEL_ID, R.string.download_channel_name);
    }
    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtil.getDownloadManager(this);
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected Notification getForegroundNotification(DownloadManager.TaskState[] taskStates) {
        return DownloadNotificationUtil.buildProgressNotification(this,R.mipmap.ic_ruto,
                DOWNLOAD_CHANNEL_ID,null,null,taskStates);
    }
}
