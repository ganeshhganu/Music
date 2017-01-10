package musiq.my.com.musiq.ui.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.manager.preference.PreferenceManager;
import musiq.my.com.musiq.ui.MediaCallback;
import musiq.my.com.musiq.ui.activity.PlayerActivity;

public class StreamingService extends Service implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener {

    private MediaPlayer mMediaPlayer;
    private IBinder mBinder = new LocalBinder();
    private MediaCallback mMediaCallback;
    private boolean isAutoChange = true;
    private TelephonyManager mgr;
    private boolean isPaused;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private static final String TRACK_NAME = "Track Name : %s";
    private static final String ARTIST_NAME = "Artist : %s";

    public StreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("completed", "-------------");
        Cursor cursor = getCursor();
        int location = PreferenceManager.getInt(getApplicationContext(), AppConstants.POSITION) + 1;
        cursor.moveToPosition(location);

        if (cursor.getCount() > location && isAutoChange) {
            start(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    location);
        }

        if (cursor.getCount() == location) {
            if (mMediaCallback != null) {
                mMediaCallback.onAlbumEnd();
            }
        } else {
            if (mMediaCallback != null) {
                mMediaCallback.onSongChange();
            }
        }

        isAutoChange = true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();
        mMediaPlayer.setOnCompletionListener(this);
        if (!isAutoChange) {
            onCompletion(mp);
        }

        if (mMediaCallback != null) {
            mMediaCallback.onMediaStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(111);
        }
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void start(int mediaID, int albumID, int mCurrentPosition) {
        if (mediaID != -1) {
            PreferenceManager.setBoolean(getApplicationContext(), AppConstants.IS_PLAYING, true);
            PreferenceManager.setInt(getApplicationContext(), AppConstants.ALBUM_ID, albumID);
            PreferenceManager.setInt(getApplicationContext(), AppConstants.MEDIA_ID, mediaID);
            PreferenceManager.setInt(getApplicationContext(), AppConstants.POSITION, mCurrentPosition);
            Cursor cursor = getCursor();
            if (cursor != null &&
                    cursor.getCount() > 0 &&
                    cursor.moveToPosition(mCurrentPosition)) {
                String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String trackName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                initNotificationBuilder(trackName, artistName);
            }
            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaID);

            initMediaPlayer(contentUri);
        }

    }

    private void initMediaPlayer(Uri contentUri) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnSeekCompleteListener(this);
                mMediaPlayer.setOnErrorListener(this);

                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(getApplicationContext(), contentUri);
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mMediaPlayer != null &&
                mMediaPlayer.isPlaying()) {
            PreferenceManager.setBoolean(getApplicationContext(), AppConstants.IS_PLAYING, false);
            isPaused = true;
            mMediaPlayer.pause();
            if (mMediaCallback != null) {
                mMediaCallback.onMediaPaused();
            }
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            PreferenceManager.setBoolean(getApplicationContext(), AppConstants.IS_PLAYING, true);
            isPaused = false;
            /*if (mCurrentPosition != 0) {
                mMediaPlayer.seekTo(mCurrentPosition);
            }else{
                mMediaPlayer.stop();
            }*/
            if (mMediaCallback != null) {
                mMediaCallback.onMediaResumed();
            }
            mMediaPlayer.start();
        }
    }

    public void stop() {
        if (mMediaPlayer != null &&
                mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        PreferenceManager.setBoolean(getApplicationContext(), AppConstants.IS_PLAYING, false);
    }

    public void seekTo(int position) {
        if (mMediaPlayer != null &&
                mMediaPlayer.getDuration() > position) {
            mMediaPlayer.seekTo(position);
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public int getCurrentDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : -1;
    }

    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : -1;
    }

    public void setMediaCallback(MediaCallback mediaCallback) {
        this.mMediaCallback = mediaCallback;
    }

    public Cursor getCursor() {
        return Utils.getSongsInAlbum(getApplicationContext(),
                PreferenceManager.getInt(getApplicationContext(),
                        AppConstants.ALBUM_ID));
    }

    public void setAutoChange(boolean autoChange) {
        isAutoChange = autoChange;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public class LocalBinder extends Binder {
        public StreamingService getService() {
            return StreamingService.this;
        }
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                pause();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                resume();
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initNotificationBuilder(String trackName, String artistName) {

        mBuilder.setSmallIcon(R.drawable.ic_logo)
                .setOngoing(true)
                .setContentTitle(String.format(TRACK_NAME, trackName))
                .setContentText(String.format(ARTIST_NAME, artistName))
                .setLargeIcon(((BitmapDrawable) getApplicationContext().getDrawable(R.drawable.bg_default_album_art)).getBitmap())
                .setSmallIcon(R.drawable.bg_default_album_art)
                .addAction(R.drawable.ic_skip_previous_black_24dp, null, null)
                .addAction(R.drawable.ic_play_arrow_black_24dp, null, null)
                .addAction(R.drawable.ic_skip_next_black_24dp, null, null);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PlayerActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(PlayerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(111, mBuilder.build());
    }

}
