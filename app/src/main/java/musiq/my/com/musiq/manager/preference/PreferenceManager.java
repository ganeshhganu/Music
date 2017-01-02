package musiq.my.com.musiq.manager.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tringapps-admin on 30/12/16.
 */

public class PreferenceManager {

    private static String preference = "musiq";

    private static SharedPreferences.Editor getPreferenceEditor(Context ctx) {
        return ctx.getSharedPreferences(preference, Context.MODE_PRIVATE).edit();
    }

    private static SharedPreferences getPreference(Context ctx) {
        return ctx.getSharedPreferences(preference, Context.MODE_PRIVATE);
    }

    public static void setString(Context ctx, String key, String value) {
        SharedPreferences.Editor preferences = getPreferenceEditor(ctx);
        preferences.putString(key, value);
        preferences.apply();
    }

    public static void setInt(Context ctx, String key, int value) {
        SharedPreferences.Editor preferences = getPreferenceEditor(ctx);
        preferences.putInt(key, value);
        preferences.apply();
    }

    public static int getInt(Context ctx, String key) {
        SharedPreferences preferences = getPreference(ctx);
        return preferences.getInt(key, -1);
    }


}
