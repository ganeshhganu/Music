package musiq.my.com.musiq.ui.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.manager.preference.PreferenceManager;
import musiq.my.com.musiq.ui.MediaCallback;

public class StreamingService extends Service implements AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener {

    private MediaPlayer mMediaPlayer;
    private IBinder mBinder = new LocalBinder();
    private int mCurrentPosition;
    private MediaCallback mMediaCallback;
    private boolean isAutoChange = true;

    public StreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
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

        if(cursor.getCount() == location){
            if (mMediaCallback != null) {
                mMediaCallback.onAlbumEnd();
            }
        }else{
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
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
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
        try {
            if (mediaID != -1) {
                PreferenceManager.setInt(getApplicationContext(), AppConstants.ALBUM_ID, albumID);
                PreferenceManager.setInt(getApplicationContext(), AppConstants.MEDIA_ID, mediaID);
                PreferenceManager.setInt(getApplicationContext(), AppConstants.POSITION, mCurrentPosition);

                Uri contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaID);

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mMediaPlayer != null &&
                mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mMediaCallback != null) {
                mMediaCallback.onMediaPaused();
            }
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            if (mCurrentPosition != 0) {
                mMediaPlayer.seekTo(mCurrentPosition);
            }
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

    public class LocalBinder extends Binder {
        public StreamingService getService() {
            return StreamingService.this;
        }
    }

}
