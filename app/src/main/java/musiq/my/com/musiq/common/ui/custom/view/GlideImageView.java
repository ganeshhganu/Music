package musiq.my.com.musiq.common.ui.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import musiq.my.com.musiq.R;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class GlideImageView extends ImageView {

    private String mSize;

    public GlideImageView(Context context) {
        super(context);
    }

    public GlideImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GlideImageView);
        mSize = array.getString(R.styleable.GlideImageView_size);
    }

    public GlideImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSize != null && mSize.equals(getContext().getString(R.string.fill))) {
            if(widthMeasureSpec>heightMeasureSpec){
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            }else{
                super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }


    public void loadImage(Uri uri) {
        Glide.with(getContext())
                .load(uri)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.bg_default_album_art)
                .crossFade()
                .into(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#c4000000"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
