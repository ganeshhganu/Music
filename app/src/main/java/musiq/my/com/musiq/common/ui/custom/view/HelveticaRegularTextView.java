package musiq.my.com.musiq.common.ui.custom.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tringapps-admin on 28/12/16.
 */

public class HelveticaRegularTextView extends TextView {

    public HelveticaRegularTextView(Context context) {
        super(context);
        init();
    }

    public HelveticaRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "helevetica_regular" + ".ttf");
        setTypeface(tf);
    }

}
