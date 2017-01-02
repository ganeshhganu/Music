package musiq.my.com.musiq.manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static musiq.my.com.musiq.manager.database.DbHelper.DatabaseConfig.DB_NAME;
import static musiq.my.com.musiq.manager.database.DbHelper.DatabaseConfig.version;

/**
 * Created by tringapps-admin on 30/12/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public interface DatabaseConfig {
        String DB_NAME = "MUSIQ.db";
        int version = 1;
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DbManager.createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
