package musiq.my.com.musiq.ui.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.launcher.Launcher;
import musiq.my.com.musiq.common.ui.Base.BaseActivity;
import musiq.my.com.musiq.common.ui.custom.view.DiskPlayer;
import musiq.my.com.musiq.common.ui.custom.view.Playlist;
import musiq.my.com.musiq.common.ui.custom.view.WaveformView;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.manager.preference.PreferenceManager;
import musiq.my.com.musiq.ui.MediaCallback;
import musiq.my.com.musiq.ui.adapter.SongListAdapter;
import musiq.my.com.musiq.ui.service.StreamingService;

public class PlayerActivity extends BaseActivity implements ServiceConnection,
        MediaCallback,
        View.OnClickListener,
        SongListAdapter.OnItemClickListener {

    private ImageView mBigAlbumArt;
    private DiskPlayer mDiskPlayer;
    private TextView mTrackName, mArtistName, mDuration, mTrackDuration;
    private StreamingService mService;
    private Handler mHandler;
    private WaveformView mWaveformView;
    private Cursor mCursor;
    private ProgressBar mTrackProgress;
    private Playlist mPlaylist;
    private boolean isBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mBigAlbumArt = (ImageView) findViewById(R.id.big_album_art);
        mDiskPlayer = (DiskPlayer) findViewById(R.id.disk_player);
        mTrackName = (TextView) findViewById(R.id.track_name);
        mArtistName = (TextView) findViewById(R.id.artist_name);
        mDuration = (TextView) findViewById(R.id.track_duration);
        mTrackDuration = (TextView) findViewById(R.id.total_duration);
        mWaveformView = (WaveformView) findViewById(R.id.wave_view);
        mTrackProgress = (ProgressBar) findViewById(R.id.track_progress);
        mPlaylist = (Playlist) findViewById(R.id.playlist_view);

        ImageView mPrevious = (ImageView) findViewById(R.id.previous);
        ImageView mNext = (ImageView) findViewById(R.id.next);
        ImageView mPause = (ImageView) findViewById(R.id.pause);

        if (mPrevious != null) {
            mPrevious.setOnClickListener(this);
        }

        if (mNext != null) {
            mNext.setOnClickListener(this);
        }

        if (mPause != null) {
            mPause.setOnClickListener(this);
        }


        mTrackProgress.setMax(100);
        mDiskPlayer.setMediaCallback(this);
        mHandler = new Handler();
    }

    final Runnable mRunnable = new Runnable() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void run() {
            mDuration.setText(Utils.getTimeTextFromMilliSeconds(mService.getCurrentDuration()));
            int progressPosition = (int) (((float) mService.getCurrentDuration() / (float) mService.getDuration()) * 100);
            mTrackProgress.setProgress(progressPosition);
            if (mService.isPlaying() &&
                    !PlayerActivity.this.isFinishing() &&
                    !PlayerActivity.this.isDestroyed()) {
                mWaveformView.updateAmplitude(1f, false);
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void loadBackgroundImage() {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");


        if (mCursor != null && mCursor.getCount() > 0) {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, mCursor.getInt(mCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)));
            Utils.loadBlurImage(this, uri, mBigAlbumArt);
            Utils.loadBlurImage(this, uri, mPlaylist.getDrawer());
            mDiskPlayer.loadRoundImage(this, uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            prepareRevealEffect();
        }
        mDiskPlayer.startAnimate();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isBound = true;
        if (service != null) {
            mService = ((StreamingService.LocalBinder) service).getService();
            mService.setMediaCallback(this);

            if (PreferenceManager.getInt(getApplicationContext(), AppConstants.MEDIA_ID) !=
                    getIntent().getIntExtra(AppConstants.MEDIA_ID, -1) ||
                    (!PreferenceManager.getBoolean(this, AppConstants.IS_PLAYING) &&
                            getIntent().getExtras().getString(AppConstants.IS_FROM).equals(AppConstants.HOME))) {
                mService.start(getIntent().getIntExtra(AppConstants.MEDIA_ID, -1),
                        getIntent().getIntExtra(AppConstants.ALBUM_ID, -1),
                        getIntent().getIntExtra(AppConstants.POSITION, -1));
            }
            updateUi();
        }
    }

    private void updateUi() {
        mDiskPlayer.setStreamingService(mService);
        mPlaylist.setService(mService);

        mCursor = mService.getCursor();
        if (mCursor != null && mCursor.getCount() > 0) {
            SongListAdapter mAdapter = new SongListAdapter(mCursor, new WeakReference<Context>(this));
            mPlaylist.getPlaylist().setAdapter(mAdapter);
            mAdapter.setItemClickListner(this);

            mCursor.moveToPosition(PreferenceManager.getInt(this, AppConstants.POSITION));
            loadBackgroundImage();
            mArtistName.setText(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
            mTrackName.setText(mCursor.getString(mCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            mTrackDuration.setText(Utils.getTimeFromMilliSeconds(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
        }
        mHandler.postDelayed(mRunnable, 100);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    @Override
    protected void onDestroy() {
        if (mService != null && !mService.isPlaying()) {
            mService.stop();
        }
        if (mCursor != null) {
            mCursor.close();
        }

        if (isBound) {
            unbindService(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null && mService.isPlaying()) {
            mDiskPlayer.resume();
        }
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(this, "Grant storage permission", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionGranted() {
        Launcher.launchPlayerService(this, getIntent(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDiskPlayer.pause();
    }

    @Override
    public void onSongChange() {
        Log.e("onSongChange", "-------------");
        int mediaID = PreferenceManager.getInt(getApplicationContext(), AppConstants.MEDIA_ID);
        getIntent().putExtra(AppConstants.MEDIA_ID, mediaID);
        updateUi();
    }

    @Override
    public void onMediaPaused() {
        Log.e("onMediaPaused", "-------------");
        mWaveformView.updateAmplitude(0f, true);
    }

    @Override
    public void onMediaResumed() {
        Log.e("onMediaResumed", "-------------");
        mHandler.postDelayed(mRunnable, 100);
        mDiskPlayer.resume();
        mWaveformView.updateAmplitude(1f, false);
    }

    @Override
    public void onMediaStart() {
        Log.e("onMediaStart", "-------------");
    }

    @Override
    public void onAlbumEnd() {
        Log.e("onAlbumEnd", "-------------");
        mDiskPlayer.pause();
        mWaveformView.updateAmplitude(0f, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                mPlaylist.performNext();
                break;

            case R.id.previous:
                mPlaylist.performPrevious();
                break;

            case R.id.pause:
                mPlaylist.performPause();
                break;
        }
    }

    @Override
    public void itemClickListener(Cursor cursor, int position) {
        mCursor = cursor;
        if (cursor != null && mService != null) {
            PreferenceManager.setInt(this, AppConstants.POSITION, position);
            mCursor.moveToPosition(position);
            mService.start(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                    position);

            updateUi();
            onSongChange();
            mPlaylist.toggleDrawer(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void prepareRevealEffect() {

        // get the center for the clipping circle
        int cx = mBigAlbumArt.getWidth();
        int cy = mBigAlbumArt.getHeight();

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(mBigAlbumArt, cx, cy, 0, finalRadius);
        anim.setDuration(1000);
        // make the view visible and start the animation
        mBigAlbumArt.setVisibility(View.VISIBLE);
        anim.start();
    }
}
