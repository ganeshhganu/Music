package musiq.my.com.musiq.manager.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tringapps-admin on 30/12/16.
 */

public class DbManager {


    public static void createTables(SQLiteDatabase database) {
        database.execSQL(DbConstants.PLAYLIST_QUERY);
    }

}
