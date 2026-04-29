package it.tecnosystemi.TS.Threads;

import android.util.Base64;
import android.util.Log;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.Utils.Functions;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebClientDevWrapper {
    static String logTag = "SERVICE";

    public static Response getNewHttpClient(String str, String str2, String str3, String str4, int i, int i2) {
        String str5 = logTag;
        Log.d(str5, str + str2);
        if (i == 0) {
            return sendGet(str, str3, str4, i2);
        }
        if (i == 1) {
            return sendPost(str, str2, str3, str4, i2, -1);
        }
        if (i == 2) {
            return sendDelete(str, str3, str4, i2, str2);
        }
        if (i != 3) {
            return null;
        }
        try {
            return sendPut(str, str2, str3, str4, i2);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(logTag, e.toString());
            return null;
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(10:1|2|(1:4)(1:5)|6|7|8|9|10|11|(2:13|(5:15|(1:17)|29|18|19)(1:20))(2:21|(2:22|(1:24)(3:30|25|26)))) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0085 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.Response sendGet(java.lang.String r8, java.lang.String r9, java.lang.String r10, int r11) throws java.lang.Exception {
        /*
            java.lang.String r0 = ""
            java.lang.String r1 = "RESP GET : "
            java.lang.String r2 = "Basic "
            java.lang.String r3 = ":PwdProAir"
            r4 = 0
            it.tecnosystemi.TS.Model.Response r5 = new it.tecnosystemi.TS.Model.Response     // Catch:{ Exception -> 0x00f7 }
            r5.<init>()     // Catch:{ Exception -> 0x00f7 }
            java.net.URL r6 = new java.net.URL     // Catch:{ Exception -> 0x00f7 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x00f7 }
            java.net.URLConnection r8 = r6.openConnection()     // Catch:{ Exception -> 0x00f7 }
            java.net.HttpURLConnection r8 = (java.net.HttpURLConnection) r8     // Catch:{ Exception -> 0x00f7 }
            r6 = 15000(0x3a98, float:2.102E-41)
            r8.setConnectTimeout(r6)     // Catch:{ Exception -> 0x00f7 }
            r8.setReadTimeout(r6)     // Catch:{ Exception -> 0x00f7 }
            r6 = 0
            r8.setUseCaches(r6)     // Catch:{ Exception -> 0x00f7 }
            if (r10 == 0) goto L_0x0037
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f7 }
            r7.<init>()     // Catch:{ Exception -> 0x00f7 }
            r7.append(r10)     // Catch:{ Exception -> 0x00f7 }
            r7.append(r3)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = r7.toString()     // Catch:{ Exception -> 0x00f7 }
            goto L_0x0039
        L_0x0037:
            java.lang.String r10 = "UsrProAir:PwdProAir"
        L_0x0039:
            byte[] r10 = r10.getBytes()     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = android.util.Base64.encodeToString(r10, r6)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r3 = "\n"
            java.lang.String r10 = r10.replace(r3, r0)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r3 = "Authorization"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f7 }
            r7.<init>(r2)     // Catch:{ Exception -> 0x00f7 }
            r7.append(r10)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = r7.toString()     // Catch:{ Exception -> 0x00f7 }
            r8.setRequestProperty(r3, r10)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = "Token"
            r8.setRequestProperty(r10, r9)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r9 = "GET"
            r8.setRequestMethod(r9)     // Catch:{ Exception -> 0x00f7 }
            r8.setDoOutput(r6)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r9 = "UserObj-Agent"
            java.lang.String r10 = "benincapp"
            r8.setRequestProperty(r9, r10)     // Catch:{ Exception -> 0x00f7 }
            r8.connect()     // Catch:{ Exception -> 0x00f7 }
            int r9 = r8.getResponseCode()     // Catch:{ Exception -> 0x00f7 }
            r5.setHttpResponceCode(r9)     // Catch:{ Exception -> 0x00f7 }
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0085 }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0085 }
            java.io.InputStream r2 = r8.getInputStream()     // Catch:{ Exception -> 0x0085 }
            r10.<init>(r2)     // Catch:{ Exception -> 0x0085 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x0085 }
            goto L_0x0093
        L_0x0085:
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00f7 }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00f7 }
            java.io.InputStream r2 = r8.getErrorStream()     // Catch:{ Exception -> 0x00f7 }
            r10.<init>(r2)     // Catch:{ Exception -> 0x00f7 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x00f7 }
        L_0x0093:
            r10 = 5
            if (r11 != r10) goto L_0x00c8
            int r9 = r5.getHttpResponceCode()     // Catch:{ Exception -> 0x00f7 }
            r10 = 200(0xc8, float:2.8E-43)
            if (r9 != r10) goto L_0x00c7
            int r9 = r8.getContentLength()     // Catch:{ Exception -> 0x00f7 }
            java.io.InputStream r8 = r8.getInputStream()     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = "fwleng"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f7 }
            r11.<init>(r0)     // Catch:{ Exception -> 0x00f7 }
            r11.append(r9)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x00f7 }
            android.util.Log.d(r10, r11)     // Catch:{ Exception -> 0x00f7 }
            byte[] r10 = new byte[r9]     // Catch:{ Exception -> 0x00f7 }
        L_0x00b9:
            if (r6 >= r9) goto L_0x00c3
            int r11 = r9 - r6
            int r11 = r8.read(r10, r6, r11)     // Catch:{ Exception -> 0x00f7 }
            int r6 = r6 + r11
            goto L_0x00b9
        L_0x00c3:
            r5.setByteResponsePayload(r10)     // Catch:{ Exception -> 0x00f7 }
            return r5
        L_0x00c7:
            return r4
        L_0x00c8:
            java.lang.StringBuffer r8 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x00f7 }
            r8.<init>()     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r10 = logTag     // Catch:{ Exception -> 0x00f7 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f7 }
            r11.<init>(r1)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r0 = r8.toString()     // Catch:{ Exception -> 0x00f7 }
            r11.append(r0)     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x00f7 }
            android.util.Log.d(r10, r11)     // Catch:{ Exception -> 0x00f7 }
        L_0x00e2:
            java.lang.String r10 = r9.readLine()     // Catch:{ Exception -> 0x00f7 }
            if (r10 == 0) goto L_0x00ec
            r8.append(r10)     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00e2
        L_0x00ec:
            r9.close()     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x00f7 }
            r5.setHttpResponcePayload(r8)     // Catch:{ Exception -> 0x00f7 }
            return r5
        L_0x00f7:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.WebClientDevWrapper.sendGet(java.lang.String, java.lang.String, java.lang.String, int):it.tecnosystemi.TS.Model.Response");
    }

    public static Response sendPost(String str, String str2, String str3, String str4, int i, int i2) throws Exception {
        String str5;
        String str6;
        Response response = new Response();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        if (str4 != null) {
            str5 = str4 + ":PwdProAir";
        } else {
            str5 = "UsrProAir:PwdProAir";
        }
        String str7 = "";
        httpURLConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(str5.getBytes(), 0).replace("\n", str7));
        httpURLConnection.setRequestProperty("Token", str3);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        if (str2 != null) {
            httpURLConnection.setRequestProperty(HttpHeaderParser.HEADER_CONTENT_TYPE, "application/json");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(str2);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
        httpURLConnection.connect();
        new Gson();
        response.setHttpResponceCode(httpURLConnection.getResponseCode());
        try {
            str6 = Functions.streamToString(httpURLConnection.getInputStream());
        } catch (Exception unused) {
            str6 = Functions.streamToString(httpURLConnection.getErrorStream());
        }
        String str8 = logTag;
        StringBuilder sb = new StringBuilder("RESP POST : ");
        if (str6 != null) {
            str7 = str6.toString();
        }
        sb.append(str7);
        Log.d(str8, sb.toString());
        response.setHttpResponcePayload(str6);
        return response;
    }

    private static Response sendPut(String str, String str2, String str3, String str4, int i) throws Exception {
        String str5;
        String str6;
        Response response = new Response();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        if (str4 != null) {
            str5 = str4 + ":PwdProAir";
        } else {
            str5 = "UsrProAir:PwdProAir";
        }
        String str7 = "";
        httpURLConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(str5.getBytes(), 0).replace("\n", str7));
        httpURLConnection.setRequestProperty("Token", str3);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        if (str2 != null) {
            httpURLConnection.setRequestProperty(HttpHeaderParser.HEADER_CONTENT_TYPE, "application/json");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(str2);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
        httpURLConnection.connect();
        new Gson();
        response.setHttpResponceCode(httpURLConnection.getResponseCode());
        try {
            str6 = Functions.streamToString(httpURLConnection.getInputStream());
        } catch (Exception unused) {
            str6 = Functions.streamToString(httpURLConnection.getErrorStream());
        }
        String str8 = logTag;
        StringBuilder sb = new StringBuilder("RESP PUT : ");
        if (str6 != null) {
            str7 = str6.toString();
        }
        sb.append(str7);
        Log.d(str8, sb.toString());
        response.setHttpResponcePayload(str6);
        return response;
    }

    private static Response sendDelete(String str, String str2, String str3, int i, String str4) throws Exception {
        String str5;
        String str6;
        Response response = new Response();
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        if (str3 != null) {
            str5 = str3 + ":PwdProAir";
        } else {
            str5 = "UsrProAir:PwdProAir";
        }
        String str7 = "";
        httpURLConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(str5.getBytes(), 0).replace("\n", str7));
        httpURLConnection.setRequestProperty("Token", str2);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("DELETE");
        if (str4 != null) {
            httpURLConnection.setRequestProperty(HttpHeaderParser.HEADER_CONTENT_TYPE, "application/json");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(str4);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
        httpURLConnection.connect();
        new Gson();
        response.setHttpResponceCode(httpURLConnection.getResponseCode());
        try {
            str6 = Functions.streamToString(httpURLConnection.getInputStream());
        } catch (Exception unused) {
            str6 = Functions.streamToString(httpURLConnection.getErrorStream());
        }
        String str8 = logTag;
        StringBuilder sb = new StringBuilder("RESP DELETE : ");
        if (str6 != null) {
            str7 = str6.toString();
        }
        sb.append(str7);
        Log.d(str8, sb.toString());
        response.setHttpResponcePayload(str6);
        return response;
    }
}
