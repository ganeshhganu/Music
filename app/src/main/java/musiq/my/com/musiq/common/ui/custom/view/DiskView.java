package musiq.my.com.musiq.common.ui.custom.view;

import android.animation.ValueAnimator;
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

import musiq.my.com.musiq.R;

/**
 * Created by tringapps-admin on 28/12/16.
 */

public class DiskView extends ImageView {

    private ValueAnimator animator;
    private Bitmap bitmap;

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

    private void init() {
        bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.vinyle_rings)).getBitmap();
        animator = new ValueAnimator();
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

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(12f);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, (canvas.getWidth() / 2) - (12f/2), paint1);

        Paint imagePaint = new Paint();
        imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        canvas.drawBitmap(getResizedBitmap(bitmap, canvas.getWidth(), canvas.getHeight()),
                canvas.getClipBounds().left,
                canvas.getClipBounds().top,
                imagePaint);
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
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(2700);
        animator.setFloatValues(360);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DiskView.this.setRotation((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void pauseAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (animator != null && animator.isRunning()) {
                animator.pause();
            }
        }
    }

    public void resumeAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (animator != null && animator.isPaused()) {
                animator.resume();
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
}
