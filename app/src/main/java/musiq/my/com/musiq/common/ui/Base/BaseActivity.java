package musiq.my.com.musiq.common.ui.Base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST_READ_STORAGE = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }

        }
    }

    public abstract void onPermissionDenied();

    public abstract void onPermissionGranted();

}
