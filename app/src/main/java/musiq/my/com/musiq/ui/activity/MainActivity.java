package musiq.my.com.musiq.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import musiq.my.com.musiq.R;
import musiq.my.com.musiq.common.launcher.Launcher;
import musiq.my.com.musiq.common.ui.Base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Launcher.launchAlbumList(MainActivity.this, null);
                finish();
            }
        }, 1000);
    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void onPermissionGranted() {

    }
}
