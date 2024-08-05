package com.example.tunevault;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    private static final String CHANNEL_ID = "MusicServiceChannel";
    public MediaPlayer mediaPlayer;
    public int currSongIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int getCurrSongIndex() {
        return currSongIndex;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if ("PLAY".equals(action)) {
            playSong();
        } else if ("NEXT".equals(action)) {
            playNextSong();
        }
        else if ("PAUSE".equals(action)) {
            pause();
        }

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TuneVault")
                .setContentText("Your music is running in the background")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void playSong() {
        if (!MainActivity.listOfSongs.isEmpty()) {
            currSongIndex = 0;
            playCurrentSong();
        }
    }

    private void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

    }

    private void playNextSong(){
        if (mediaPlayer != null) {
            currSongIndex = (currSongIndex + 1) % MainActivity.listOfSongs.size();
            playCurrentSong();
        }
    }

    private void playCurrentSong() {
        if (currSongIndex < MainActivity.listOfSongs.size()) {
            Uri songUri = MainActivity.listOfSongs.get(currSongIndex);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, songUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    playNextSong();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
