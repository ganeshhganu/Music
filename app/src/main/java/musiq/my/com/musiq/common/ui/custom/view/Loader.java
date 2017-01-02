package musiq.my.com.musiq.common.ui.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tringapps-admin on 2/1/17.
 */

public class Loader extends View {

    private Paint mPaint;

    private float radian = 5f;
    private boolean isShrinking;
    private int a = 3;

    private List<Float> radius;

    public Loader(Context context) {
        super(context);
        init();
    }

    public Loader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Loader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#fd4401"));
        radius = new ArrayList<>(a);
        for (int i = 0; i < a; i++) {
            radius.add(0f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int position = (a % 2 == 0) ? canvas.getWidth() / (a + a) : canvas.getWidth() / ((a - 1) + (a - 1));
        Log.e("TAG", "position: " + position);
        if (radian < 20f && !isShrinking) {
            /*canvas.drawCircle(position / 2, canvas.getHeight() / 2, radian += 1, mPaint);
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radian += 1, mPaint);
            canvas.drawCircle(canvas.getWidth() - position / 2, canvas.getHeight() / 2, radian += 1, mPaint);*/
            for (int i = 1; i <= a; i++) {
                canvas.drawCircle(canvas.getWidth() - i * (position), canvas.getHeight() / 2, radian += 1, mPaint);
                Log.e("TAG", radius.get(i-1) + "onDraw: " + (canvas.getWidth() - i * (position)));
                for (int j = 1; j <= a; j++) {
                    if (radian > 10f) {
                        radius.add(j-1, radian);
                    }
                }
            }
        } else {
            radian = 5f;
        }
        invalidate();
    }
}
