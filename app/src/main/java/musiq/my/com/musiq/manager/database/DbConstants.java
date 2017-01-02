package musiq.my.com.musiq.manager.database;

/**
 * Created by tringapps-admin on 30/12/16.
 */

public class DbConstants {

    public static String PLAYLIST = "PLAYLIST";
    public static String MEDIA_ID = "MEDIA_ID";
    public static String PLAYLIST_QUERY = "CREATE TABLE IF NOT EXISTS" + PLAYLIST + "( " + MEDIA_ID + " INTEGER NOT NULL)";

}
