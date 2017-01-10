package musiq.my.com.musiq.ui;

/**
 * Created by tringapps-admin on 2/1/17.
 */

public interface MediaCallback {
    void onSongChange();

    void onMediaPaused();

    void onMediaResumed();

    void onMediaStart();

    void onAlbumEnd();
}
