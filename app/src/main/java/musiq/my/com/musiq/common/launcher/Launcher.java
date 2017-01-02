package musiq.my.com.musiq.common.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import musiq.my.com.musiq.ui.activity.PlayerActivity;
import musiq.my.com.musiq.ui.activity.AlbumListActivity;
import musiq.my.com.musiq.ui.activity.SongListActivity;
import musiq.my.com.musiq.ui.service.StreamingService;

/**
 * Created by tringapps-admin on 27/12/16.
 */

public class Launcher {

    public static void launchPlayer(Context context, Intent lIntent) {
        Intent intent = new Intent(context, PlayerActivity.class);
        if (lIntent != null) {
            intent.putExtras(lIntent);
        }
        context.startActivity(intent);
    }

    public static void launchAlbumList(Context context, Intent lIntent) {
        Intent intent = new Intent(context, AlbumListActivity.class);
        if (lIntent != null) {
            intent.putExtras(lIntent);
        }
        context.startActivity(intent);
    }

    public static void launchSongList(Context context, Intent lIntent) {
        Intent intent = new Intent(context, SongListActivity.class);
        if (lIntent != null) {
            intent.putExtras(lIntent);
        }
        context.startActivity(intent);
    }

    public static void launchPlayerService(Context context, Intent lIntent, ServiceConnection connection) {
        Intent intent = new Intent(context, StreamingService.class);
        if (lIntent != null) {
            intent.putExtras(lIntent);
        }
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }
}
