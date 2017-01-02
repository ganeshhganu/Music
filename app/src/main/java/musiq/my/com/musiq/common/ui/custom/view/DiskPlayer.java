package musiq.my.com.musiq.common.ui.custom.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.ui.MediaCallback;
import musiq.my.com.musiq.ui.service.StreamingService;

/**
 * Created by tringapps-admin on 28/12/16.
 */

public class DiskPlayer extends RelativeLayout implements MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener {

    private DiskView mDiskView;
    private ImageView mDiskArm, mIndicator;
    private ValueAnimator animator;
    private StreamingService mStreamingService;
    private GestureDetector mDetector;
    private GestureDetector.SimpleOnGestureListener mListener;
    private MediaCallback mPlayBackCallback;

    public DiskPlayer(Context context) {
        super(context);
        init();
    }

    public DiskPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiskPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.disk_player, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDiskView = (DiskView) findViewById(R.id.disk_view);
        mDiskArm = (ImageView) findViewById(R.id.disk_arm);
        mIndicator = (ImageView) findViewById(R.id.indicator);
        mDiskArm.setRotation(20f);
        mIndicator.setRotation(12f);
        mDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mStreamingService.isPlaying()) {
                    mStreamingService.pause();
                    pause();
                    if (mPlayBackCallback != null) {
                        mPlayBackCallback.onMediaPaused();
                    }
                } else {
                    mStreamingService.resume();
                    resume();
                    if (mPlayBackCallback != null) {
                        mPlayBackCallback.onMediaResumed();
                    }
                }
                return super.onDoubleTap(e);
            }
        });
    }

    public void loadRoundImage(Context ctx, Uri uri) {
        mDiskView.loadRoundImage(ctx, uri);
    }

    public void startAnimate() {
        mDiskView.startAnimate();
    }

    public void updateIndicator(boolean isOn) {
        mIndicator.setImageResource(isOn ?
                R.drawable.gold_skin_light_on :
                R.drawable.gold_skin_light_off);
    }

    public void placeArm(final float value, final boolean reverse) {
        mDiskArm.setPivotX(mDiskArm.getWidth() / 2);
        mDiskArm.setPivotY(mDiskArm.getWidth() / 2);
        animator = new ValueAnimator();
        animator.setDuration(700);
        animator.setFloatValues(value);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (reverse) {
                    mDiskView.pauseAnimation();
                } else {
                    mDiskView.resumeAnimation();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = reverse ?
                        value - (Float) animation.getAnimatedValue() :
                        (Float) animation.getAnimatedValue();
                mDiskArm.setRotation(animatedValue);
            }
        });
        animator.start();
    }

    private double startAngle, previousAngle;
    private int count;
    private boolean isFromPlayed;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mStreamingService.isPlaying()) {
                    mDiskView.pauseAnimation();
                    if (mPlayBackCallback != null) {
                        mPlayBackCallback.onMediaPaused();
                    }
                    isFromPlayed = true;
                }
                count = 0;
                startAngle = getAngle(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                double currentAngle = getAngle(event.getX(), event.getY());
                if (mStreamingService.getMediaPlayer() != null &&
                        mStreamingService.getMediaPlayer().getCurrentPosition() + count > 0 &&
                        mStreamingService.getMediaPlayer()
                                .getCurrentPosition() + count < mStreamingService.getMediaPlayer()
                                .getDuration()) {
                    mDiskView.setRotation(-(float) currentAngle);
                }
                startAngle = currentAngle;
                if (currentAngle - previousAngle > 300) {
                    count += 10000;
                } else if (Math.abs(previousAngle - currentAngle) > 300) {
                    count -= 10000;
                }
                previousAngle = currentAngle;
                return true;


            case MotionEvent.ACTION_UP:
                if (isFromPlayed) {
                    mStreamingService.seekTo(mStreamingService.getMediaPlayer().getCurrentPosition() + count);
                    isFromPlayed = false;
                    if (mPlayBackCallback != null) {
                        mPlayBackCallback.onMediaResumed();
                    }
                }
                return true;

        }
        return super.onTouchEvent(event);
    }

    /**
     * @return The angle of the unit circle with the image view's center
     */
    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - (getWidth() / 2d);
        double y = getHeight() - yTouch - (getHeight() / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    /**
     * @return The selected quadrant.
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    public void setStreamingService(StreamingService streamingService) {
        this.mStreamingService = streamingService;
        this.mStreamingService.getMediaPlayer().setOnSeekCompleteListener(this);
        this.mStreamingService.getMediaPlayer().setOnCompletionListener(this);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.start();
        mDiskView.resumeAnimation();
    }

    public void resume() {
        updateIndicator(true);
        placeArm(20f, false);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        pause();
    }

    public void pause() {
        placeArm(20f, true);
        updateIndicator(false);
    }

    public void setMediaCallback(MediaCallback mPlayBackCallback) {
        this.mPlayBackCallback = mPlayBackCallback;
    }
}

