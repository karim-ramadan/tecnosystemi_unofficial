package it.tecnosystemi.TS.Commands;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.PICO.PicoActivity;
import it.tecnosystemi.TS.Utils.Functions;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class UDPSocket {
    static final String CMDSTATO = "stato_sync";
    public static final long DEFTIMEOUTACK = 5000;
    public static final long DEFTIMEOUTCMD = 5000;
    static final String cmdJson = "cmd";
    static DatagramSocket datagramSocketSend = null;
    static boolean gotackcmd = false;
    static boolean gotackstato = false;
    static Gson gson = null;
    static long idp = 1;
    static final String idpkJson = "idp";
    static String ipDest = "192.168.4.1";
    static boolean isSlave = false;
    static boolean islistening = false;
    static boolean keeplistening = false;
    static long lastCmdIdp = -1;
    static String lastCmdResp = null;
    static String lastcmd = null;
    static int portaDest = 40070;
    static int portaRead = 40069;
    static final String resJson = "res";
    static DatagramSocket socketListen;
    static Activity statoActivity;
    static List<Long> statoIdp;
    static Thread threadRead;

    public static void startListening() {
        startListening(false);
    }

    public static void startListening(boolean z) {
        startListening(z, false, (String) null);
    }

    public static void startListening(boolean z, boolean z2, String str) {
        Log.d("UDP", "startListening _isSlave:" + z2);
        if (!islistening || z) {
            isSlave = z2;
            statoIdp = Collections.synchronizedList(new ArrayList());
            if (isSlave) {
                ipDest = str;
            } else {
                ipDest = "192.168.4.1";
            }
            try {
                Thread thread = threadRead;
                if (thread != null) {
                    thread.interrupt();
                }
            } catch (Exception unused) {
            }
            threadRead = null;
            Thread thread2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (UDPSocket.socketListen != null) {
                            UDPSocket.socketListen.disconnect();
                            UDPSocket.socketListen.close();
                        }
                    } catch (Exception unused) {
                    }
                    try {
                        UDPSocket.socketListen = null;
                        UDPSocket.keeplistening = true;
                        InetAddress iPAddress = Functions.getIPAddress(true);
                        if (UDPSocket.isSlave) {
                            UDPSocket.socketListen = new DatagramSocket(UDPSocket.portaDest, iPAddress);
                        } else {
                            UDPSocket.socketListen = new DatagramSocket(UDPSocket.portaRead, iPAddress);
                        }
                        UDPSocket.socketListen.setSoTimeout(1000);
                        while (UDPSocket.keeplistening && UDPSocket.socketListen != null) {
                            try {
                                byte[] bArr = new byte[ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED];
                                UDPSocket.socketListen.receive(new DatagramPacket(bArr, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED));
                                UDPSocket.parsemsg(new String(bArr, 0, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED));
                            } catch (SocketTimeoutException unused2) {
                            } catch (SocketException unused3) {
                            } catch (Exception e) {
                                Log.d("SOCKET - UDP READ", e.toString());
                            }
                        }
                        UDPSocket.islistening = false;
                    } catch (Exception unused4) {
                    }
                }
            });
            threadRead = thread2;
            thread2.start();
        }
    }

    /* access modifiers changed from: private */
    public static void parsemsg(final String str) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    Log.e("SOCKET - UDP", "<---- " + jSONObject);
                    if (jSONObject.has(UDPSocket.idpkJson)) {
                        long j = jSONObject.getLong(UDPSocket.idpkJson);
                        if (UDPSocket.statoIdp.contains(Long.valueOf(j))) {
                            if (jSONObject.has(UDPSocket.resJson)) {
                                if (jSONObject.getLong(UDPSocket.resJson) == 99) {
                                    UDPSocket.gotackstato = true;
                                } else {
                                    UDPSocket.statoIdp.remove(Long.valueOf(j));
                                    if (UDPSocket.statoActivity instanceof PicoActivity) {
                                        ((PicoActivity) UDPSocket.statoActivity).receiveStato(jSONObject.toString());
                                    }
                                }
                            }
                        } else if (j == UDPSocket.lastCmdIdp) {
                            if (jSONObject.getLong(UDPSocket.resJson) == 99) {
                                UDPSocket.gotackcmd = true;
                                Log.d("SOCKET", "GOT ACK!");
                            } else {
                                UDPSocket.lastCmdResp = jSONObject.toString();
                            }
                        }
                        if (jSONObject.has(UDPSocket.resJson) && jSONObject.getLong(UDPSocket.resJson) != 99) {
                            UDPSocket.sendAk(jSONObject);
                        }
                    }
                } catch (Exception e) {
                    Log.d("SOCKET - UDP PARSE", e.toString());
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public static void sendAk(JSONObject jSONObject) {
        try {
            sendAk(jSONObject.getLong(idpkJson), jSONObject.getString(cmdJson));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendAk(long j, String str) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(idpkJson, j);
            jSONObject.put("frm", "app");
            jSONObject.put(resJson, 99);
            sendCMD((CmdPICO) null, 5000, 5000, true, 1, true, jSONObject);
        } catch (Exception e) {
            Log.d("SOCKET - UDP", "---->" + e.toString());
        }
    }

    public static void stopListening() {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Log.d("SOCKET - UDP", "stopListening " + methodName);
        keeplistening = false;
        islistening = false;
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        UDPSocket.socketListen.disconnect();
                        UDPSocket.socketListen.close();
                    } catch (Exception unused) {
                    }
                    UDPSocket.socketListen = null;
                }
            }).start();
            Thread.sleep(500);
        } catch (Exception unused) {
        }
    }

    public static void resetIDP() {
        idp = 1;
        statoIdp = Collections.synchronizedList(new ArrayList());
    }

    public static synchronized String sendCMD(CmdPICO cmdPICO) {
        String sendCMD;
        synchronized (UDPSocket.class) {
            sendCMD = sendCMD(cmdPICO, 5000);
        }
        return sendCMD;
    }

    public static synchronized String sendCMD(CmdPICO cmdPICO, long j) {
        String sendCMD;
        synchronized (UDPSocket.class) {
            sendCMD = sendCMD(cmdPICO, j, 5000);
        }
        return sendCMD;
    }

    public static synchronized String sendCMD(CmdPICO cmdPICO, long j, long j2) {
        String sendCMD;
        synchronized (UDPSocket.class) {
            sendCMD = sendCMD(cmdPICO, j, j2, false);
        }
        return sendCMD;
    }

    public static synchronized String sendCMD(CmdPICO cmdPICO, long j, long j2, boolean z) {
        String sendCMD;
        synchronized (UDPSocket.class) {
            sendCMD = sendCMD(cmdPICO, j, j2, false, 3);
        }
        return sendCMD;
    }

    public static synchronized String sendCMD(CmdPICO cmdPICO, long j, long j2, boolean z, int i) {
        String sendCMD;
        synchronized (UDPSocket.class) {
            try {
                sendCMD = sendCMD(cmdPICO, j, j2, z, i, false, (JSONObject) null);
            } catch (Throwable th) {
                throw th;
            }
        }
        return sendCMD;
    }

    public static String sendCMD(CmdPICO cmdPICO, long j, long j2, boolean z, int i, boolean z2, JSONObject jSONObject) {
        return sendCMDAsink(cmdPICO, j, j2, z, i, z2, jSONObject);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:43|44|45|46|(2:48|(2:73|71))(1:(2:68|52)(3:53|54|(2:75|71)))|50|74|71) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x011b */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x011f A[Catch:{ Exception -> 0x0153 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0134 A[Catch:{ Exception -> 0x0153 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String sendCMDAsink(it.tecnosystemi.TS.Commands.CmdPICO r19, long r20, long r22, boolean r24, int r25, boolean r26, org.json.JSONObject r27) {
        /*
            r0 = r19
            r1 = -1
            r3 = 0
            java.lang.String r4 = ipDest     // Catch:{ Exception -> 0x0153 }
            java.net.InetAddress r4 = java.net.InetAddress.getByName(r4)     // Catch:{ Exception -> 0x0153 }
            com.google.gson.Gson r5 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0153 }
            r5.<init>()     // Catch:{ Exception -> 0x0153 }
            gson = r5     // Catch:{ Exception -> 0x0153 }
            r5 = 0
            r6 = r25
            r7 = 0
        L_0x0016:
            if (r7 >= r6) goto L_0x0164
            r8 = 100
            r10 = 1
            if (r7 == 0) goto L_0x002a
            stopListening()     // Catch:{ Exception -> 0x0153 }
            java.lang.Thread.sleep(r8)     // Catch:{ Exception -> 0x0153 }
            boolean r11 = isSlave     // Catch:{ Exception -> 0x0153 }
            java.lang.String r12 = ipDest     // Catch:{ Exception -> 0x0153 }
            startListening(r10, r11, r12)     // Catch:{ Exception -> 0x0153 }
        L_0x002a:
            if (r26 != 0) goto L_0x0036
            long r11 = idp     // Catch:{ Exception -> 0x0153 }
            r13 = 1
            long r13 = r13 + r11
            idp = r13     // Catch:{ Exception -> 0x0153 }
            r0.setIdp(r11)     // Catch:{ Exception -> 0x0153 }
        L_0x0036:
            java.lang.String r11 = "---->"
            java.lang.String r12 = "SOCKET - UDP"
            if (r26 == 0) goto L_0x0051
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0153 }
            r13.<init>()     // Catch:{ Exception -> 0x0153 }
            r13.append(r11)     // Catch:{ Exception -> 0x0153 }
            r14 = r27
            r13.append(r14)     // Catch:{ Exception -> 0x0153 }
            java.lang.String r11 = r13.toString()     // Catch:{ Exception -> 0x0153 }
            android.util.Log.d(r12, r11)     // Catch:{ Exception -> 0x0153 }
            goto L_0x006b
        L_0x0051:
            r14 = r27
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0153 }
            r13.<init>()     // Catch:{ Exception -> 0x0153 }
            r13.append(r11)     // Catch:{ Exception -> 0x0153 }
            com.google.gson.Gson r11 = gson     // Catch:{ Exception -> 0x0153 }
            java.lang.String r11 = r11.toJson((java.lang.Object) r0)     // Catch:{ Exception -> 0x0153 }
            r13.append(r11)     // Catch:{ Exception -> 0x0153 }
            java.lang.String r11 = r13.toString()     // Catch:{ Exception -> 0x0153 }
            android.util.Log.d(r12, r11)     // Catch:{ Exception -> 0x0153 }
        L_0x006b:
            if (r26 == 0) goto L_0x0076
            java.lang.String r11 = r27.toString()     // Catch:{ Exception -> 0x0153 }
            byte[] r11 = r11.getBytes()     // Catch:{ Exception -> 0x0153 }
            goto L_0x0090
        L_0x0076:
            com.google.gson.Gson r11 = gson     // Catch:{ Exception -> 0x0153 }
            java.lang.String r11 = r11.toJson((java.lang.Object) r0)     // Catch:{ Exception -> 0x0153 }
            byte[] r11 = r11.getBytes()     // Catch:{ Exception -> 0x0153 }
            java.lang.String r13 = r19.getCmd()     // Catch:{ Exception -> 0x0153 }
            lastcmd = r13     // Catch:{ Exception -> 0x0153 }
            long r15 = r19.getIdp()     // Catch:{ Exception -> 0x0153 }
            lastCmdIdp = r15     // Catch:{ Exception -> 0x0153 }
            lastCmdResp = r3     // Catch:{ Exception -> 0x0153 }
            gotackcmd = r5     // Catch:{ Exception -> 0x0153 }
        L_0x0090:
            java.net.DatagramSocket r13 = datagramSocketSend     // Catch:{ Exception -> 0x0153 }
            if (r13 != 0) goto L_0x009b
            java.net.DatagramSocket r13 = new java.net.DatagramSocket     // Catch:{ Exception -> 0x0153 }
            r13.<init>()     // Catch:{ Exception -> 0x0153 }
            datagramSocketSend = r13     // Catch:{ Exception -> 0x0153 }
        L_0x009b:
            boolean r13 = isSlave     // Catch:{ Exception -> 0x0153 }
            if (r13 == 0) goto L_0x00a8
            java.net.DatagramPacket r13 = new java.net.DatagramPacket     // Catch:{ Exception -> 0x0153 }
            int r15 = r11.length     // Catch:{ Exception -> 0x0153 }
            int r5 = portaRead     // Catch:{ Exception -> 0x0153 }
            r13.<init>(r11, r15, r4, r5)     // Catch:{ Exception -> 0x0153 }
            goto L_0x00b0
        L_0x00a8:
            java.net.DatagramPacket r13 = new java.net.DatagramPacket     // Catch:{ Exception -> 0x0153 }
            int r5 = r11.length     // Catch:{ Exception -> 0x0153 }
            int r15 = portaDest     // Catch:{ Exception -> 0x0153 }
            r13.<init>(r11, r5, r4, r15)     // Catch:{ Exception -> 0x0153 }
        L_0x00b0:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0153 }
            r5.<init>()     // Catch:{ Exception -> 0x0153 }
            java.net.InetAddress r11 = r13.getAddress()     // Catch:{ Exception -> 0x0153 }
            r5.append(r11)     // Catch:{ Exception -> 0x0153 }
            java.lang.String r11 = " "
            r5.append(r11)     // Catch:{ Exception -> 0x0153 }
            int r11 = r13.getPort()     // Catch:{ Exception -> 0x0153 }
            r5.append(r11)     // Catch:{ Exception -> 0x0153 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0153 }
            android.util.Log.d(r12, r5)     // Catch:{ Exception -> 0x0153 }
            r5 = 0
        L_0x00d0:
            r11 = 5
            if (r5 >= r11) goto L_0x00f8
            java.net.DatagramSocket r11 = datagramSocketSend     // Catch:{ Exception -> 0x0153 }
            boolean r11 = r11.isConnected()     // Catch:{ Exception -> 0x0153 }
            if (r11 != 0) goto L_0x00e8
            java.net.DatagramSocket r11 = datagramSocketSend     // Catch:{ Exception -> 0x0153 }
            java.net.InetAddress r15 = r13.getAddress()     // Catch:{ Exception -> 0x0153 }
            int r10 = r13.getPort()     // Catch:{ Exception -> 0x0153 }
            r11.connect(r15, r10)     // Catch:{ Exception -> 0x0153 }
        L_0x00e8:
            java.net.DatagramSocket r10 = datagramSocketSend     // Catch:{ Exception -> 0x0153 }
            boolean r10 = r10.isConnected()     // Catch:{ Exception -> 0x0153 }
            if (r10 == 0) goto L_0x00f1
            goto L_0x00f8
        L_0x00f1:
            java.lang.Thread.sleep(r8)     // Catch:{ Exception -> 0x0153 }
            int r5 = r5 + 1
            r10 = 1
            goto L_0x00d0
        L_0x00f8:
            java.net.DatagramSocket r5 = datagramSocketSend     // Catch:{ Exception -> 0x0153 }
            r5.send(r13)     // Catch:{ Exception -> 0x0153 }
            java.lang.String r5 = "PK SENT!"
            android.util.Log.d(r12, r5)     // Catch:{ Exception -> 0x0153 }
            if (r26 == 0) goto L_0x0107
            java.lang.String r0 = ""
            return r0
        L_0x0107:
            java.util.Date r5 = new java.util.Date     // Catch:{ Exception -> 0x0153 }
            r5.<init>()     // Catch:{ Exception -> 0x0153 }
            r10 = 1
        L_0x010d:
            if (r10 == 0) goto L_0x014e
            java.lang.String r11 = lastCmdResp     // Catch:{ Exception -> 0x0153 }
            if (r11 == 0) goto L_0x0118
            lastcmd = r3     // Catch:{ Exception -> 0x0153 }
            lastCmdIdp = r1     // Catch:{ Exception -> 0x0153 }
            return r11
        L_0x0118:
            java.lang.Thread.sleep(r8)     // Catch:{ Exception -> 0x011b }
        L_0x011b:
            boolean r11 = gotackcmd     // Catch:{ Exception -> 0x0153 }
            if (r11 != 0) goto L_0x0134
            java.util.Date r11 = new java.util.Date     // Catch:{ Exception -> 0x0153 }
            r11.<init>()     // Catch:{ Exception -> 0x0153 }
            long r11 = r11.getTime()     // Catch:{ Exception -> 0x0153 }
            long r17 = r5.getTime()     // Catch:{ Exception -> 0x0153 }
            long r11 = r11 - r17
            int r13 = (r11 > r22 ? 1 : (r11 == r22 ? 0 : -1))
            if (r13 <= 0) goto L_0x010d
        L_0x0132:
            r10 = 0
            goto L_0x010d
        L_0x0134:
            if (r24 == 0) goto L_0x0139
            java.lang.String r0 = "OK"
            return r0
        L_0x0139:
            java.util.Date r7 = new java.util.Date     // Catch:{ Exception -> 0x0153 }
            r7.<init>()     // Catch:{ Exception -> 0x0153 }
            long r11 = r7.getTime()     // Catch:{ Exception -> 0x0153 }
            long r17 = r5.getTime()     // Catch:{ Exception -> 0x0153 }
            long r11 = r11 - r17
            r7 = 3
            int r13 = (r11 > r20 ? 1 : (r11 == r20 ? 0 : -1))
            if (r13 <= 0) goto L_0x010d
            goto L_0x0132
        L_0x014e:
            int r7 = r7 + 1
            r5 = 0
            goto L_0x0016
        L_0x0153:
            r0 = move-exception
            java.lang.String r4 = "SOCKET - UDP SEND"
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r4, r0)
            java.net.DatagramSocket r0 = datagramSocketSend     // Catch:{ Exception -> 0x0162 }
            r0.disconnect()     // Catch:{ Exception -> 0x0162 }
        L_0x0162:
            datagramSocketSend = r3
        L_0x0164:
            lastCmdIdp = r1
            lastcmd = r3
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Commands.UDPSocket.sendCMDAsink(it.tecnosystemi.TS.Commands.CmdPICO, long, long, boolean, int, boolean, org.json.JSONObject):java.lang.String");
    }

    public static String sendGetStato(String str) {
        DatagramPacket datagramPacket;
        try {
            CmdPICO cmdPICO = new CmdPICO();
            cmdPICO.setPin(str);
            long j = idp;
            idp = 1 + j;
            cmdPICO.setIdp(j);
            cmdPICO.setCmd("stato_sync");
            String json = new Gson().toJson((Object) cmdPICO);
            Log.d("SOCKET - UDP", "---->" + json);
            statoIdp.add(Long.valueOf(cmdPICO.getIdp()));
            InetAddress byName = InetAddress.getByName(ipDest);
            DatagramSocket datagramSocket = new DatagramSocket();
            byte[] bytes = json.toString().getBytes();
            if (isSlave) {
                datagramPacket = new DatagramPacket(bytes, bytes.length, byName, portaRead);
            } else {
                datagramPacket = new DatagramPacket(bytes, bytes.length, byName, portaDest);
            }
            datagramSocket.send(datagramPacket);
            return null;
        } catch (Exception e) {
            Log.d("UDP SEND GET STATO", e.toString());
            return null;
        }
    }

    public static void setStatoActivity(Activity activity) {
        statoActivity = activity;
    }

    public static boolean isConnected() {
        try {
            return InetAddress.getByName(ipDest).isReachable(2000);
        } catch (Exception unused) {
            return false;
        }
    }
}
