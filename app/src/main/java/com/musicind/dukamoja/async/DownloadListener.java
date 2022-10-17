package com.musicind.dukamoja.async;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import static android.content.Context.NOTIFICATION_SERVICE;

public class DownloadListener {

    private DownloadService downloadService = null;

    private int lastProgress = 0;
    private String CHANNEL_ID = "com.musicind.dukamoja.async";
    NotificationManager notificationManager;


    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public DownloadService getDownloadService() {
        return downloadService;
    }

    public void onSuccess()
    {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download success.", -1);
    }

    public void onFailed()
    {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download failed.", -1);
    }
    public void onPaused()
    {
        sendDownloadNotification("Download paused.", lastProgress);
    }
    public void onCanceled()
    {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download canceled.", -1);
    }

    public void onUpdateDownloadProgress(int progress)
    {
        try {
            lastProgress = progress;
            sendDownloadNotification("Downloading...", progress);

            // Thread sleep 0.2 seconds to let Pause, Continue and Cancel button in notification clickable.
            Thread.sleep(200);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }



    public void sendDownloadNotification(String title, int progress)
    {
        if (isAndroidOOrHigher()){
          createChannel();
        }
        Notification notification = getDownloadNotification(title, progress);
        notificationManager.notify(167, notification);

    }

    private boolean isAndroidOOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DOWNLOADSESSION";
            String description = "testchanneldescription";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager = (NotificationManager)downloadService.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification getDownloadNotification(String title, int progress)
    {


        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(downloadService, CHANNEL_ID);
        notifyBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

        Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), android.R.drawable.stat_sys_download);
        notifyBuilder.setLargeIcon(bitmap);

        notifyBuilder.setContentIntent(pendingIntent);
        notifyBuilder.setContentTitle(title);
        notifyBuilder.setFullScreenIntent(pendingIntent, true);

        if(progress > 0 && progress < 100)
        {
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("Download progress ");
            stringBuffer.append(progress);
            stringBuffer.append("%");

            notifyBuilder.setContentText("Download progress " + progress + "%");

            notifyBuilder.setProgress(100, progress, false);

            // Add Pause download button intent in notification.
            Intent pauseDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            pauseDownloadIntent.setAction(DownloadService.ACTION_PAUSE_DOWNLOAD);
            PendingIntent pauseDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, pauseDownloadIntent, 0);
            NotificationCompat.Action pauseDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pauseDownloadPendingIntent);
            notifyBuilder.addAction(pauseDownloadAction);

            // Add Continue download button intent in notification.
            Intent continueDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            continueDownloadIntent.setAction(DownloadService.ACTION_CONTINUE_DOWNLOAD);
            PendingIntent continueDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, continueDownloadIntent, 0);
            NotificationCompat.Action continueDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Continue", continueDownloadPendingIntent);
            notifyBuilder.addAction(continueDownloadAction);

            // Add Cancel download button intent in notification.
            Intent cancelDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            cancelDownloadIntent.setAction(DownloadService.ACTION_CANCEL_DOWNLOAD);
            PendingIntent cancelDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, cancelDownloadIntent, 0);
            NotificationCompat.Action cancelDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_delete, "Cancel", cancelDownloadPendingIntent);
            notifyBuilder.addAction(cancelDownloadAction);
        }

        return notifyBuilder.build();
    }

}