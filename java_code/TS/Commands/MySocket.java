package it.tecnosystemi.TS.Commands;

import android.app.Activity;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Commands.Cmd;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MySocket {
    public static final int BUFFER_SIZE = 1000;
    public static String IP_actual = "";
    public static String PORT_actual = null;
    public static final int SOCKET_MORE_TIMEOUT = 15000;
    public static final int SOCKET_TIMEOUT = 1000;
    public static final int SOCKET_TIMES = 1;
    public static String TIMEOUTERR = "timeouterr";
    public static Activity activity = null;
    private static MySocket instance = null;
    public static BaseActivity mcon = null;
    private static SavePreferences pref = null;
    public static int timeouttimes = 3;
    public static Network tobindnet = null;
    public static final int totaltimeouttimes = 3;
    public static boolean useLong = false;

    public static void initInstance(BaseActivity baseActivity, Activity activity2, boolean z) {
        if (instance == null || z) {
            instance = new MySocket();
            mcon = baseActivity;
            activity = activity2;
            BaseActivity baseActivity2 = mcon;
            pref = new SavePreferences(baseActivity2, baseActivity2.getString(R.string.PrefsName));
        }
    }

    private MySocket() {
    }

    static class MySocketReadAndWrite extends AsyncTask<String, Void, String> {
        MySocketReadAndWrite() {
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0050, code lost:
            if (r5.isConnected() == false) goto L_0x0052;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String doInBackground(java.lang.String... r13) {
            /*
                r12 = this;
                r0 = 3
                r1 = 1
                r0 = r13[r0]     // Catch:{ Exception -> 0x000d }
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x000d }
                int r0 = r0.intValue()     // Catch:{ Exception -> 0x000d }
                goto L_0x000f
            L_0x000d:
                r0 = 1
            L_0x000f:
                r2 = 4
                r2 = r13[r2]
                boolean r2 = it.tecnosystemi.TS.Utils.Constants._LOG
                r3 = 0
                java.lang.String r4 = "SOCKET"
                if (r2 == 0) goto L_0x0036
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                java.lang.String r5 = "@@@@@ IP: "
                r2.<init>(r5)
                r5 = r13[r1]
                r2.append(r5)
                java.lang.String r5 = " WRITE "
                r2.append(r5)
                r5 = r13[r3]
                r2.append(r5)
                java.lang.String r2 = r2.toString()
                android.util.Log.e(r4, r2)
            L_0x0036:
                java.lang.String r2 = ""
                r5 = 0
            L_0x0039:
                r6 = r2
            L_0x003a:
                boolean r7 = r6.equals(r2)
                if (r7 == 0) goto L_0x0144
                if (r0 <= 0) goto L_0x0144
                int r0 = r0 + -1
                java.lang.String r7 = " CMD: "
                java.lang.String r8 = " EXCEPTION "
                java.lang.String r9 = "TENTATIVO "
                if (r5 == 0) goto L_0x0052
                boolean r10 = r5.isConnected()     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                if (r10 != 0) goto L_0x005f
            L_0x0052:
                r10 = r13[r1]     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                r11 = 2
                r11 = r13[r11]     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                int r11 = java.lang.Integer.parseInt(r11)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                java.net.Socket r5 = it.tecnosystemi.TS.Commands.MySocket.connectWithSocket(r10, r11)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
            L_0x005f:
                if (r5 == 0) goto L_0x003a
                boolean r10 = r5.isConnected()     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                if (r10 == 0) goto L_0x003a
                r10 = r13[r3]     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                it.tecnosystemi.TS.Commands.MySocket.sendDataToSocket(r10, r5)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                r10 = r13[r3]     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                java.lang.String r6 = it.tecnosystemi.TS.Commands.MySocket.receiveDataFromSocket(r10, r5)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                boolean r10 = it.tecnosystemi.TS.Utils.Constants._LOG     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                if (r10 == 0) goto L_0x003a
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                r10.<init>()     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                java.lang.String r11 = "#### READ: "
                r10.append(r11)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                r10.append(r6)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                java.lang.String r10 = r10.toString()     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                android.util.Log.e(r4, r10)     // Catch:{ ConnectException -> 0x011c, SocketTimeoutException -> 0x00da, SocketException -> 0x00b2, Exception -> 0x008b }
                goto L_0x003a
            L_0x008b:
                r6 = move-exception
                boolean r10 = it.tecnosystemi.TS.Utils.Constants._LOG
                if (r10 == 0) goto L_0x0039
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>(r9)
                r10.append(r0)
                r10.append(r8)
                java.lang.String r6 = r6.toString()
                r10.append(r6)
                r10.append(r7)
                r6 = r13[r3]
                r10.append(r6)
                java.lang.String r6 = r10.toString()
                android.util.Log.e(r4, r6)
                goto L_0x0039
            L_0x00b2:
                r6 = move-exception
                boolean r10 = it.tecnosystemi.TS.Utils.Constants._LOG
                if (r10 == 0) goto L_0x0039
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>(r9)
                r10.append(r0)
                r10.append(r8)
                java.lang.String r6 = r6.toString()
                r10.append(r6)
                r10.append(r7)
                r6 = r13[r3]
                r10.append(r6)
                java.lang.String r6 = r10.toString()
                android.util.Log.e(r4, r6)
                goto L_0x0039
            L_0x00da:
                r6 = move-exception
                java.lang.Throwable r10 = r6.getCause()
                if (r10 == 0) goto L_0x00f2
                java.lang.Throwable r10 = r6.getCause()
                boolean r10 = r10 instanceof java.net.SocketTimeoutException
                if (r10 == 0) goto L_0x00f2
                boolean r10 = it.tecnosystemi.TS.Utils.Constants._LOG
                if (r10 == 0) goto L_0x00f2
                java.lang.String r10 = "READ TIMEOUT"
                android.util.Log.e(r4, r10)
            L_0x00f2:
                java.lang.String r10 = it.tecnosystemi.TS.Commands.MySocket.TIMEOUTERR
                boolean r11 = it.tecnosystemi.TS.Utils.Constants._LOG
                if (r11 == 0) goto L_0x0119
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>(r9)
                r11.append(r0)
                r11.append(r8)
                java.lang.String r6 = r6.toString()
                r11.append(r6)
                r11.append(r7)
                r6 = r13[r3]
                r11.append(r6)
                java.lang.String r6 = r11.toString()
                android.util.Log.e(r4, r6)
            L_0x0119:
                r6 = r10
                goto L_0x003a
            L_0x011c:
                r10 = move-exception
                boolean r11 = it.tecnosystemi.TS.Utils.Constants._LOG
                if (r11 == 0) goto L_0x003a
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>(r9)
                r11.append(r0)
                r11.append(r8)
                java.lang.String r8 = r10.toString()
                r11.append(r8)
                r11.append(r7)
                r7 = r13[r3]
                r11.append(r7)
                java.lang.String r7 = r11.toString()
                android.util.Log.e(r4, r7)
                goto L_0x003a
            L_0x0144:
                it.tecnosystemi.TS.Commands.MySocket.disconnectWithSocket(r5)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Commands.MySocket.MySocketReadAndWrite.doInBackground(java.lang.String[]):java.lang.String");
        }
    }

    public static boolean tryConnectWithSocket(String str, int i) throws IOException {
        Socket socket = new Socket();
        try {
            new InetSocketAddress(InetAddress.getByName(str), i);
            Socket connectWithSocket = connectWithSocket(str, i);
            if (connectWithSocket == null || !connectWithSocket.isConnected()) {
                if (!connectWithSocket.isClosed()) {
                    connectWithSocket.close();
                }
                if (!Constants._LOG) {
                    return false;
                }
                Log.d("beninca - socket", "socket chiusa");
                return false;
            }
            connectWithSocket.close();
            if (!connectWithSocket.isClosed()) {
                connectWithSocket.close();
            }
            if (!Constants._LOG) {
                return true;
            }
            Log.d("beninca - socket", "socket chiusa");
            return true;
        } catch (Exception e) {
            if (Constants._LOG) {
                Log.d("beninca - socket", e.toString());
            }
            if (!socket.isClosed()) {
                socket.close();
            }
            if (!Constants._LOG) {
                return false;
            }
        } catch (Throwable th) {
            if (!socket.isClosed()) {
                socket.close();
            }
            if (Constants._LOG) {
                Log.d("beninca - socket", "socket chiusa");
            }
            throw th;
        }
    }

    public static Socket connectWithSocket(String str, int i) throws Exception {
        Network network;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(str), i);
        Socket socket = new Socket();
        int i2 = 0;
        while (i2 < 2) {
            try {
                if (Build.VERSION.SDK_INT >= 21 && (network = tobindnet) != null) {
                    network.bindSocket(socket);
                }
            } catch (Exception e) {
                try {
                    Log.d("SOCKET", e.toString());
                } catch (Exception unused) {
                    try {
                        Thread.sleep(800);
                    } catch (Exception unused2) {
                    }
                    socket = new Socket();
                    i2++;
                }
            }
            if (useLong) {
                useLong = false;
                socket.setSoTimeout(15000);
            } else {
                socket.setSoTimeout(1000);
            }
            socket.connect(inetSocketAddress, 1000);
            return socket;
        }
        return socket;
    }

    public static void disconnectWithSocket(Socket socket) {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                if (Constants._LOG) {
                    Log.d("beninca", e.toString());
                }
            }
        }
    }

    public static void sendDataToSocket(String str, Socket socket) throws IOException {
        if (str != null && socket.isConnected()) {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.write(str);
            printWriter.flush();
        }
    }

    public static String receiveDataFromSocket(String str, Socket socket) throws IOException {
        byte[] bArr = new byte[1000];
        InputStream inputStream = socket.getInputStream();
        System.currentTimeMillis();
        new DataInputStream(socket.getInputStream());
        String str2 = "";
        while (true) {
            try {
                int read = inputStream.read(bArr, 0, 1000);
                str2 = str2 + new String(Arrays.copyOfRange(bArr, 0, read), "UTF-8");
                if (read < 1000) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception unused) {
                }
            } catch (Exception e) {
                Log.e("SOCKET", "Read exception: " + e.getMessage());
            }
        }
        Log.e("SOCKET", str2);
        Log.d("SOCKET", str2);
        return str2;
    }

    public static String sendAndReceive(String str, String str2, int i) {
        try {
            MySocketReadAndWrite mySocketReadAndWrite = new MySocketReadAndWrite();
            Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
            return (String) mySocketReadAndWrite.executeOnExecutor(executor, new String[]{str, str2, i + "", Constants.COMMAND10MIN, ""}).get();
        } catch (InterruptedException e) {
            Log.d("tecnocystemy", e.toString());
            return "";
        } catch (ExecutionException e2) {
            Log.d("tecnocystemy", e2.toString());
            return "";
        }
    }

    public static String getWifi(String str, int i) {
        try {
            if (Constants._LOG) {
                Log.e("SOCKET", "GET WIFI");
            }
            Cmd.GetWifi getWifi = new Cmd.GetWifi();
            getWifi.command = Protocols.CMD_GET_WIFI;
            getWifi.pin = "-1";
            String json = new Gson().toJson((Object) getWifi);
            useLong = true;
            return sendAndReceive(json, str, i);
        } catch (Exception e) {
            if (!Constants._LOG) {
                return null;
            }
            Log.d("WIFI", e.toString());
            return null;
        }
    }

    public static String configCu(String str, boolean z, WiFi wiFi, String str2, int i) {
        String str3;
        try {
            if (Constants._LOG) {
                Log.e("SOCKET", "GET WIFI");
            }
            Cmd.ConfigCuOnLine configCuOnLine = new Cmd.ConfigCuOnLine();
            Gson create = new GsonBuilder().disableHtmlEscaping().create();
            configCuOnLine.command = Protocols.CMD_CONFIG;
            configCuOnLine.pin = str;
            if (z) {
                configCuOnLine.offline = 1;
                str3 = create.toJson((Object) configCuOnLine);
            } else {
                configCuOnLine.offline = 0;
                if (wiFi.getPwd() == null) {
                    wiFi.setPwd("");
                }
                configCuOnLine.pwd = wiFi.getPwd();
                configCuOnLine.sec = wiFi.isCrip();
                configCuOnLine.sid = wiFi.getSid();
                configCuOnLine.mac = wiFi.getMac();
                str3 = create.toJson((Object) configCuOnLine);
            }
            useLong = true;
            return sendAndReceive(str3, str2, i);
        } catch (Exception e) {
            if (!Constants._LOG) {
                return null;
            }
            Log.d("WIFI", e.toString());
            return null;
        }
    }

    public static String checkPinCmd(String str, String str2, int i) {
        Gson gson = new Gson();
        Cmd.CheckPin checkPin = new Cmd.CheckPin();
        checkPin.command = Protocols.CMD_CHECK_PIN;
        checkPin.pin = str;
        return sendAndReceive(gson.toJson((Object) checkPin), str2, i);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:16|17|18|19|20|21) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x003e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String commandToCU(java.lang.String r5, java.lang.String r6, int r7, final boolean r8, boolean r9, boolean r10) {
        /*
            r0 = 0
            boolean r1 = it.tecnosystemi.TS.Utils.Constants._LOG     // Catch:{ Exception -> 0x0090 }
            if (r1 == 0) goto L_0x000a
            java.lang.String r1 = "SOCKETCommand"
            android.util.Log.e(r1, r5)     // Catch:{ Exception -> 0x0090 }
        L_0x000a:
            if (r9 == 0) goto L_0x0019
            java.lang.Thread r1 = new java.lang.Thread     // Catch:{ Exception -> 0x0090 }
            it.tecnosystemi.TS.Commands.MySocket$1 r2 = new it.tecnosystemi.TS.Commands.MySocket$1     // Catch:{ Exception -> 0x0090 }
            r2.<init>()     // Catch:{ Exception -> 0x0090 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0090 }
            r1.start()     // Catch:{ Exception -> 0x0090 }
        L_0x0019:
            java.lang.String r1 = sendAndReceive(r5, r6, r7)     // Catch:{ Exception -> 0x0090 }
            if (r1 == 0) goto L_0x007e
            boolean r2 = it.tecnosystemi.TS.Utils.Constants._LOG     // Catch:{ Exception -> 0x0090 }
            if (r2 == 0) goto L_0x0028
            java.lang.String r2 = "SOCKETResp"
            android.util.Log.e(r2, r1)     // Catch:{ Exception -> 0x0090 }
        L_0x0028:
            java.lang.String r2 = TIMEOUTERR     // Catch:{ Exception -> 0x0090 }
            boolean r2 = r1.equals(r2)     // Catch:{ Exception -> 0x0090 }
            r3 = 3
            r4 = 1
            if (r2 == 0) goto L_0x0048
            int r1 = timeouttimes     // Catch:{ Exception -> 0x0090 }
            int r1 = r1 - r4
            timeouttimes = r1     // Catch:{ Exception -> 0x0090 }
            if (r1 <= 0) goto L_0x0043
            r1 = 500(0x1f4, double:2.47E-321)
            java.lang.Thread.sleep(r1)     // Catch:{ Exception -> 0x003e }
        L_0x003e:
            java.lang.String r5 = commandToCU(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0090 }
            return r5
        L_0x0043:
            timeouttimes = r3     // Catch:{ Exception -> 0x0090 }
            java.lang.String r5 = ""
            return r5
        L_0x0048:
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x0090 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0090 }
            timeouttimes = r3     // Catch:{ Exception -> 0x0090 }
            java.lang.String r6 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0090 }
            boolean r6 = r5.has(r6)     // Catch:{ Exception -> 0x0090 }
            if (r6 == 0) goto L_0x006c
            java.lang.String r6 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0090 }
            int r5 = r5.getInt(r6)     // Catch:{ Exception -> 0x0090 }
            if (r5 == r4) goto L_0x0088
            if (r8 == 0) goto L_0x0088
            android.app.Activity r6 = activity     // Catch:{ Exception -> 0x0090 }
            it.tecnosystemi.TS.Commands.MySocket$2 r7 = new it.tecnosystemi.TS.Commands.MySocket$2     // Catch:{ Exception -> 0x0090 }
            r7.<init>(r5)     // Catch:{ Exception -> 0x0090 }
            r6.runOnUiThread(r7)     // Catch:{ Exception -> 0x0090 }
            goto L_0x0088
        L_0x006c:
            if (r10 == 0) goto L_0x0073
            it.tecnosystemi.TS.Activity.BaseActivity r5 = mcon     // Catch:{ Exception -> 0x0090 }
            r5.hideProgress()     // Catch:{ Exception -> 0x0090 }
        L_0x0073:
            android.app.Activity r5 = activity     // Catch:{ Exception -> 0x0090 }
            it.tecnosystemi.TS.Commands.MySocket$3 r6 = new it.tecnosystemi.TS.Commands.MySocket$3     // Catch:{ Exception -> 0x0090 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x0090 }
            r5.runOnUiThread(r6)     // Catch:{ Exception -> 0x0090 }
            return r0
        L_0x007e:
            android.app.Activity r5 = activity     // Catch:{ Exception -> 0x0090 }
            it.tecnosystemi.TS.Commands.MySocket$4 r6 = new it.tecnosystemi.TS.Commands.MySocket$4     // Catch:{ Exception -> 0x0090 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x0090 }
            r5.runOnUiThread(r6)     // Catch:{ Exception -> 0x0090 }
        L_0x0088:
            if (r10 == 0) goto L_0x008f
            it.tecnosystemi.TS.Activity.BaseActivity r5 = mcon     // Catch:{ Exception -> 0x0090 }
            r5.hideProgress()     // Catch:{ Exception -> 0x0090 }
        L_0x008f:
            return r1
        L_0x0090:
            if (r10 == 0) goto L_0x0098
            it.tecnosystemi.TS.Activity.BaseActivity r5 = mcon
            r5.hideProgress()
        L_0x0098:
            android.app.Activity r5 = activity
            it.tecnosystemi.TS.Commands.MySocket$5 r6 = new it.tecnosystemi.TS.Commands.MySocket$5
            r6.<init>(r8)
            r5.runOnUiThread(r6)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Commands.MySocket.commandToCU(java.lang.String, java.lang.String, int, boolean, boolean, boolean):java.lang.String");
    }
}
