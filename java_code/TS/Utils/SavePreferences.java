package it.tecnosystemi.TS.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import com.securepreferences.SecurePreferences;

public class SavePreferences {
    public static SharedPreferences prefs;
    private Context mContext;
    private String prefsName;

    public SavePreferences(Context context, String str) {
        if (prefs == null) {
            this.mContext = context;
            this.prefsName = str;
            try {
                if (Build.VERSION.SDK_INT > 27) {
                    prefs = new SecurePreferences(context, "proAirprefpwd12345", Settings.Secure.getString(this.mContext.getContentResolver(), "android_id"), "appProAirPref.xml", 10000);
                } else {
                    prefs = new SecurePreferences(context, "proAirprefpwd12345", "appProAirPref.xml");
                }
            } catch (Exception unused) {
            }
        }
        if (this.mContext == null) {
            this.mContext = context;
        }
    }

    public boolean save(String str, String str2) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(str, str2);
        edit.commit();
        return true;
    }

    public boolean delete(String str) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(str);
        edit.commit();
        return true;
    }

    public boolean save(String str, int i) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(str, i);
        edit.commit();
        return true;
    }

    public boolean save(String str, boolean z) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(str, z);
        edit.commit();
        return true;
    }

    public String getString(String str) {
        return prefs.getString(str, "");
    }

    public int getInt(String str) {
        return prefs.getInt(str, -999);
    }

    public int getInt(String str, int i) {
        return prefs.getInt(str, i);
    }

    public boolean getBoolean(String str) {
        return prefs.getBoolean(str, false);
    }

    public void initialize() {
        String string = getString(Constants.PREF_PWD);
        String string2 = getString(Constants.PREF_TOKEN_DATE);
        String string3 = getString(Constants.PREF_TOKEN_VALUE);
        int i = getInt(Constants.PREF_USER_ID);
        String string4 = getString(Constants.PREF_USERNAME);
        boolean z = getBoolean(Constants.PREF_REMEBERME);
        save(Constants.PREF_UC_HOME, "[]");
        prefs.edit().clear();
        for (String remove : prefs.getAll().keySet()) {
            prefs.edit().remove(remove);
        }
        prefs.edit().commit();
        save(Constants.PREF_PWD, string);
        save(Constants.PREF_TOKEN_DATE, string2);
        save(Constants.PREF_TOKEN_VALUE, string3);
        save(Constants.PREF_USER_ID, i);
        save(Constants.PREF_USERNAME, string4);
        save(Constants.PREF_REMEBERME, z);
    }

    public void deletePref() {
        save(Constants.PREF_PWD, "");
        save(Constants.PREF_TOKEN_DATE, "");
        save(Constants.PREF_TOKEN_VALUE, "");
        save(Constants.PREF_USER_ID, 0);
        save(Constants.PREF_USERNAME, "");
        save(Constants.PREF_REMEBERME, false);
    }
}
