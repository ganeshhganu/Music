package musiq.my.com.musiq.common.ui.custom.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import musiq.my.com.musiq.R;

/**
 * Created by tringapps-admin on 31/12/16.
 */

public class RightController extends RelativeLayout implements GestureDetector.OnGestureListener, View.OnClickListener {

    private GestureDetector mGestureDetector;
    private LinearLayout mContainer;
    private ValueAnimator mAnimator;
    private ImageView mMute;
    private AudioManager mAudioManager;

    public RightController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.overlay_control, this);
        mGestureDetector = new GestureDetector(getContext(), this);
        mAnimator = new ValueAnimator();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContainer = (LinearLayout) findViewById(R.id.container);
        mMute = (ImageView) findViewById(R.id.mute);
        mMute.setOnClickListener(this);

        RightController.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                RightController.this.getViewTreeObserver().removeOnPreDrawListener(this);
                RightController.this.setTranslationX(mContainer.getWidth());
                return true;
            }
        });
    }

    private float initialX, initialY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

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
        final boolean collapse = RightController.this.getTranslationX() == 0;
        mAnimator.setDuration(400);
        mAnimator.setFloatValues(mContainer.getWidth());
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = collapse ? (float) animation.getAnimatedValue() :
                        (mContainer.getWidth() - (Float) animation.getAnimatedValue());
                RightController.this.setTranslationX(animValue);
            }
        });
        if ((collapse && velocityX > 0) || (!collapse && velocityX < 0)) {
            mAnimator.start();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mute:
                if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                    mMute.setImageResource(R.drawable.ic_mute);
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    mMute.setImageResource(R.drawable.ic_mute_disable);
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                break;
        }
    }
}
