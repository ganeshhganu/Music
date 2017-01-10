package musiq.my.com.musiq.common.ui.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.AppConstants;
import musiq.my.com.musiq.common.utils.Utils;
import musiq.my.com.musiq.manager.preference.PreferenceManager;
import musiq.my.com.musiq.ui.service.StreamingService;

/**
 * Created by tringapps-admin on 31/12/16.
 */

public class Playlist extends RelativeLayout implements GestureDetector.OnGestureListener, View.OnClickListener {

    private GestureDetector mGestureDetector;
    private RelativeLayout mContainer;
    private ValueAnimator mAnimator;
    private RecyclerView mPlaylist;
    private ImageView mDrawer;
    private ImageView mPlay;
    private StreamingService mService;
    private ImageView mNextTrack;
    private ImageView mPreviousTrack;

    public Playlist(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.overlay_left_control, this);
        mGestureDetector = new GestureDetector(getContext(), this);
        mAnimator = new ValueAnimator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContainer = (RelativeLayout) findViewById(R.id.container);
        mDrawer = (ImageView) findViewById(R.id.drawer_background);
        mPreviousTrack = (ImageView) findViewById(R.id.previous);
        mNextTrack = (ImageView) findViewById(R.id.next);
        mPlay = (ImageView) findViewById(R.id.pause);
        mPlaylist = (RecyclerView) findViewById(R.id.playlist);
        mPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));

        mPreviousTrack.setOnClickListener(this);
        mNextTrack.setOnClickListener(this);
        mPlay.setOnClickListener(this);

        Playlist.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Playlist.this.getViewTreeObserver().removeOnPreDrawListener(this);
                Playlist.this.setTranslationX(-mContainer.getWidth());
                return true;
            }
        });
    }

    private float initialX, initialY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                mGestureDetector.onTouchEvent(event);
                return true;

            default:
                if (Math.abs(initialX - event.getX()) > Math.abs(initialY - event.getY()) &&
                        !mAnimator.isRunning()) {
                    mGestureDetector.onTouchEvent(event);
                }
                break;
        }


        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final boolean collapse = Playlist.this.getTranslationX() == 0;
        toggleDrawer(collapse);
        if ((collapse && velocityX < 0) || (!collapse && velocityX > 0)) {
            mAnimator.start();
        }
        return true;
    }

    public void toggleDrawer(final boolean collapse) {
        mAnimator.setDuration(400);
        mAnimator.setFloatValues(-mContainer.getWidth());
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = collapse ? (float) animation.getAnimatedValue() : (-mContainer.getWidth() - (Float) animation.getAnimatedValue());
                Playlist.this.setTranslationX(animValue);
            }
        });
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Playlist.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Playlist.this.getViewTreeObserver().removeOnPreDrawListener(this);
                Playlist.this.setTranslationX(-mContainer.getWidth());
                return true;
            }
        });
    }

    public void performPrevious() {
        int currentPosition = PreferenceManager.getInt(getContext(), AppConstants.POSITION) - 1;
        if (currentPosition != -1) {
            int albumId = PreferenceManager.getInt(getContext(), AppConstants.ALBUM_ID);
            Cursor preCursor = Utils.getSongsInAlbum(getContext(), albumId);
            preCursor.moveToPosition(currentPosition);
            /**
             * To override on complete
             */
            mService.setAutoChange(false);
            mService.start(preCursor.getInt(preCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    albumId,
                    currentPosition);
        }
    }

    public void performNext() {

        int currentPosition = PreferenceManager.getInt(getContext(), AppConstants.POSITION) + 1;
        int albumId = PreferenceManager.getInt(getContext(), AppConstants.ALBUM_ID);
        Cursor nextCursor = Utils.getSongsInAlbum(getContext(), albumId);
        if (nextCursor.getCount() > currentPosition) {
            nextCursor.moveToPosition(currentPosition);
            /**
             * To override on complete
             */
            mService.setAutoChange(false);
            mService.start(nextCursor.getInt(nextCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                    albumId,
                    currentPosition);
        }
    }

    public void performPause() {
        if (mService != null) {
            if (mService.isPlaying()) {
                mPlay.setImageResource(R.drawable.ic_fab_play);
                mService.pause();
            } else {
                mPlay.setImageResource(R.drawable.ic_fab_pause);
                mService.resume();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.next:
                performNext();
                break;

            case R.id.previous:
                performPrevious();
                break;

            case R.id.pause:
                performPause();
                break;
        }
        toggleDrawer(true);
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    public RecyclerView getPlaylist() {
        return mPlaylist;
    }

    public ImageView getDrawer() {
        return mDrawer;
    }

    public void setService(StreamingService mService) {
        this.mService = mService;
    }
}
