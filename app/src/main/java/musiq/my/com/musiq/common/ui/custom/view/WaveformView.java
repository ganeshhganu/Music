package musiq.my.com.musiq.common.ui.custom.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class WaveformView extends View {
    private static final float kDefaultFrequency = 1.5f;
    private static final float kDefaultAmplitude = 0.5f;
    private static final float kDefaultIdleAmplitude = 0.01f;
    private static final float kDefaultNumberOfWaves = 6.0f;
    private static final float kDefaultPhaseShift = -0.15f;
    private static final float kDefaultDensity = 5.0f;
    private static final float kDefaultPrimaryLineWidth = 3.0f;
    private static final float kDefaultSecondaryLineWidth = 1.0f;

    private float phase;
    private float amplitude;
    private float frequency;
    private float idleAmplitude;
    private float numberOfWaves;
    private float phaseShift;
    private float density;
    private float primaryWaveLineWidth;
    private float secondaryWaveLineWidth;
    Paint mPaintColor;
    Rect rect;
    boolean isStraightLine = false;

    public WaveformView(Context context) {
        super(context);
        setUp();
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaveformView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp();
    }

    private void setUp() {
        this.frequency = kDefaultFrequency;

        if (amplitude != 0.0f) {
            this.amplitude = kDefaultAmplitude;
        }
        this.idleAmplitude = kDefaultIdleAmplitude;

        this.numberOfWaves = kDefaultNumberOfWaves;
        this.phaseShift = kDefaultPhaseShift;
        this.density = kDefaultDensity;

        this.primaryWaveLineWidth = kDefaultPrimaryLineWidth;
        this.secondaryWaveLineWidth = kDefaultSecondaryLineWidth;
        mPaintColor = new Paint();
        mPaintColor.setColor(Color.parseColor("#4BFFFFFF"));
        mPaintColor.setStyle(Paint.Style.STROKE);
        mPaintColor.setAntiAlias(true);
    }

    public void updateAmplitude(float level, boolean isStraightLine) {
        /*this.phase += phaseShift;*/
        this.amplitude = Math.max(level, idleAmplitude);
        this.isStraightLine = isStraightLine;
        /*invalidate();*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        rect = new Rect(0, 0, canvas.getWidth(), canvas.getWidth());
        if (!isStraightLine) {
            for (int i = 0; i < numberOfWaves; i++) {
                mPaintColor.setStrokeWidth(i == 0 ? primaryWaveLineWidth : secondaryWaveLineWidth);
                float halfHeight = canvas.getHeight() / 2;
                float width = canvas.getWidth();
                float mid = width / 2;

                float maxAmplitude = halfHeight - 4.0f;
                float progress = 1.0f - (float) i / this.numberOfWaves;
                float normedAmplitude = (1.5f * progress - 0.5f) * this.amplitude;
                Path path = new Path();

                float multiplier = Math.min(1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

                for (float x = 0; x < width + density; x += density) {
                    // We use a parable to scale the sinus wave, that has its peak in the middle of the view.
                    float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                    float y = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (x / width) * frequency + phase) + halfHeight);

                    if (x == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                canvas.drawPath(path, mPaintColor);
            }
        }
        this.phase += phaseShift;
        invalidate();
    }
}
