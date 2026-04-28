package it.tecnosystemi.TS.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.WebClientDevWrapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterClient {
    private String Backend_Endpoint;
    private String authorizationHeader;
    protected HttpClient httpClient = new DefaultHttpClient();
    SharedPreferences settings;

    public RegisterClient(Context context, String str) {
        this.settings = context.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
        this.Backend_Endpoint = str + "/api/v1/";
    }

    public String getAuthorizationHeader() {
        return this.authorizationHeader;
    }

    public void setAuthorizationHeader(String str) {
        this.authorizationHeader = str;
    }

    public void register(String str, Set<String> set, Activity activity) throws ClientProtocolException, IOException, JSONException {
        String retrieveRegistrationIdOrRequestNewOne = retrieveRegistrationIdOrRequestNewOne(str, activity);
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("Platform", Constants.NOTIFIC_PLAT);
        jSONObject.put("Handle", str);
        jSONObject.put("Tags", new JSONArray(set));
        int upsertRegistration = upsertRegistration(retrieveRegistrationIdOrRequestNewOne, jSONObject, activity);
        if (upsertRegistration != 200) {
            if (upsertRegistration == 410) {
                this.settings.edit().remove(Constants.PREF_REGID_SETTING_NAME).commit();
                int upsertRegistration2 = upsertRegistration(retrieveRegistrationIdOrRequestNewOne(str, activity), jSONObject, activity);
                if (upsertRegistration2 != 200) {
                    Log.e("RegisterClient", "Error upserting registration: " + upsertRegistration2);
                    throw new RuntimeException("Error upserting registration");
                }
                return;
            }
            Log.e("RegisterClient", "Error upserting registration: " + upsertRegistration);
            throw new RuntimeException("Error upserting registration");
        }
    }

    private int upsertRegistration(String str, JSONObject jSONObject, Activity activity) throws UnsupportedEncodingException, IOException, ClientProtocolException {
        String str2;
        Resources resources = activity.getResources();
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            str2 = Constants.FIX_TOKEN;
        } else {
            str2 = Functions.calcNewToken(savePreferences, resources);
        }
        String str3 = str2;
        return WebClientDevWrapper.getNewHttpClient(this.Backend_Endpoint + "UpdateNotifications?id=" + str, jSONObject.toString(), str3, Constants.user, 3, 7).getHttpResponceCode();
    }

    public String retrieveRegistrationIdOrRequestNewOne(String str, Context context) throws ClientProtocolException, IOException {
        String str2;
        String str3 = str;
        Context context2 = context;
        Resources resources = context.getResources();
        SavePreferences savePreferences = new SavePreferences(context2, context2.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            str2 = Constants.FIX_TOKEN;
        } else {
            str2 = Functions.calcNewToken(savePreferences, resources);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.Backend_Endpoint + "RegisterNotifications?handle=" + str3, (String) null, str2, Constants.user, 1, 6);
        int i = 5;
        while (i > 0) {
            i--;
            if (newHttpClient.getHttpResponceCode() != 401) {
                break;
            }
            newHttpClient = WebClientDevWrapper.getNewHttpClient(this.Backend_Endpoint + "RegisterNotifications?handle=" + str3, (String) null, Functions.calcNewToken(savePreferences, resources), Constants.user, 1, 6);
        }
        if (newHttpClient.getHttpResponceCode() == 200) {
            String httpResponcePayload = newHttpClient.getHttpResponcePayload();
            String substring = httpResponcePayload.substring(1, httpResponcePayload.length() - 1);
            this.settings.edit().putString(Constants.PREF_REGID_SETTING_NAME, substring).commit();
            return substring;
        }
        Log.e("RegisterClient", "Error creating registrationId: " + newHttpClient.getHttpResponcePayload());
        throw new RuntimeException("Error creating Notification Hubs registrationId");
    }
}
