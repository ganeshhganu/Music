package musiq.my.com.musiq.common.ui.custom.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.lang.ref.WeakReference;

import musiq.my.com.musiq.R;

/**
 * Created by tringapps-admin on 28/12/16.
 */

public class DiskView extends ImageView implements ValueAnimator.AnimatorUpdateListener {

    private ValueAnimator animator;
    private Bitmap bitmap;
    private Paint paint1;
    private Paint imagePaint;
    private PorterDuffXfermode mask;
    private WeakReference<ValueAnimator> valueAnimatorWeakReference;
    private WeakReference<Paint> outlineWeakReference, imagePaintWeakReference;

    public DiskView(Context context) {
        super(context);
        init();
    }

    public DiskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.vinyle_rings, null)).getBitmap();
        animator = new ValueAnimator();
        valueAnimatorWeakReference = new WeakReference<>(animator);
        paint1 = new Paint();
        imagePaint = new Paint();
        outlineWeakReference = new WeakReference<>(paint1);
        imagePaintWeakReference = new WeakReference<>(imagePaint);
        mask = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    public void loadRoundImage(final Context context, Uri uri) {
        Glide.with(context)
                .load(uri)
                .asBitmap()
                .override(300, 300)
                .centerCrop()
                .placeholder(R.drawable.disk)
                .into(new BitmapImageViewTarget(this) {
                    @Override
                    public void onResourceReady(final Bitmap resource,
                                                final GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);

                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        DiskView.this.setImageDrawable(circularBitmapDrawable);
                    }

                });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        outlineWeakReference.get().setColor(Color.BLACK);
        outlineWeakReference.get().setStyle(Paint.Style.STROKE);
        outlineWeakReference.get().setStrokeWidth(12f);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, (canvas.getWidth() / 2) - (12f / 2), outlineWeakReference.get());

        imagePaintWeakReference.get().setXfermode(mask);

        canvas.drawBitmap(getResizedBitmap(bitmap, canvas.getWidth(), canvas.getHeight()),
                0,
                0,
                imagePaintWeakReference.get());
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public void startAnimate() {
        valueAnimatorWeakReference.get().setRepeatCount(ValueAnimator.INFINITE);
        valueAnimatorWeakReference.get().setDuration(2500);
        valueAnimatorWeakReference.get().setFloatValues(360);
        valueAnimatorWeakReference.get().setInterpolator(new LinearInterpolator());
        valueAnimatorWeakReference.get().addUpdateListener(this);
        valueAnimatorWeakReference.get().start();
    }

    public void pauseAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (valueAnimatorWeakReference.get() != null && valueAnimatorWeakReference.get().isRunning()) {
                valueAnimatorWeakReference.get().pause();
            }
        }
    }

    public void resumeAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (valueAnimatorWeakReference.get() != null && valueAnimatorWeakReference.get().isPaused()) {
                valueAnimatorWeakReference.get().resume();
            }
        }
    }

    private void showNotificationBuilder() {
        NotificationBuilderWithBuilderAccessor builderWithBuilderAccessor = new NotificationBuilderWithBuilderAccessor() {
            @Override
            public Notification.Builder getBuilder() {
                return null;
            }

            @Override
            public Notification build() {
                return null;
            }
        };
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        DiskView.this.setRotation((Float) animation.getAnimatedValue());
    }
}
