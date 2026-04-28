package it.tecnosystemi.TS.Commands;

import android.net.Network;
import it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity;
import java.io.OutputStream;
import java.net.Socket;

public class PicoSocketBootloader {
    public static final int SOCKET_TIMEOUT = 10000;
    static PicoBootloaderActivity bootloaderActivity = null;
    protected static final char[] hexArray = {MySocketBootLoader.cleanFlashCommand, MySocketBootLoader.writeIDCommand, MySocketBootLoader.readCRCCommand, '3', MySocketBootLoader.rebootCommand, MySocketBootLoader.bootverCommand, MySocketBootLoader.eraseFlashCommand, '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static String ip = "192.168.4.1";
    static int porta = 40060;
    public static Socket socket;
    public static Network tobindnet;

    public static void setActivity(PicoBootloaderActivity picoBootloaderActivity) {
        bootloaderActivity = picoBootloaderActivity;
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0016 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean connectToSocket(java.lang.String r2) {
        /*
            if (r2 != 0) goto L_0x0004
            java.lang.String r2 = ip
        L_0x0004:
            java.net.Socket r0 = socket     // Catch:{ Exception -> 0x0016 }
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isConnected()     // Catch:{ Exception -> 0x0016 }
            if (r0 == 0) goto L_0x0013
            java.net.Socket r0 = socket     // Catch:{ Exception -> 0x0016 }
            r0.close()     // Catch:{ Exception -> 0x0016 }
        L_0x0013:
            r0 = 0
            socket = r0     // Catch:{ Exception -> 0x0016 }
        L_0x0016:
            java.net.Socket r0 = new java.net.Socket     // Catch:{ Exception -> 0x0045 }
            r0.<init>()     // Catch:{ Exception -> 0x0045 }
            socket = r0     // Catch:{ Exception -> 0x0045 }
            java.net.InetAddress r2 = java.net.InetAddress.getByName(r2)     // Catch:{ Exception -> 0x0045 }
            java.net.InetSocketAddress r0 = new java.net.InetSocketAddress     // Catch:{ Exception -> 0x0045 }
            int r1 = porta     // Catch:{ Exception -> 0x0045 }
            r0.<init>(r2, r1)     // Catch:{ Exception -> 0x0045 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0045 }
            r1 = 21
            if (r2 < r1) goto L_0x0037
            android.net.Network r2 = tobindnet     // Catch:{ Exception -> 0x0045 }
            if (r2 == 0) goto L_0x0037
            java.net.Socket r1 = socket     // Catch:{ Exception -> 0x0045 }
            r2.bindSocket(r1)     // Catch:{ Exception -> 0x0045 }
        L_0x0037:
            java.net.Socket r2 = socket     // Catch:{ Exception -> 0x0045 }
            r1 = 10000(0x2710, float:1.4013E-41)
            r2.connect(r0, r1)     // Catch:{ Exception -> 0x0045 }
            java.net.Socket r2 = socket     // Catch:{ Exception -> 0x0045 }
            boolean r2 = r2.isConnected()     // Catch:{ Exception -> 0x0045 }
            return r2
        L_0x0045:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Commands.PicoSocketBootloader.connectToSocket(java.lang.String):boolean");
    }

    public static boolean sendfile(byte[] bArr) {
        if (!socket.isConnected()) {
            return false;
        }
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bArr);
            outputStream.flush();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static String printByteArray(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            int i2 = i * 2;
            char[] cArr2 = hexArray;
            cArr[i2] = cArr2[(b & 255) >>> 4];
            cArr[i2 + 1] = cArr2[b & 15];
        }
        return new String(cArr);
    }

    public static void closeSocket() {
        try {
            Socket socket2 = socket;
            if (socket2 != null) {
                if (socket2.isConnected()) {
                    socket.close();
                }
                socket = null;
            }
        } catch (Exception unused) {
        }
    }
}
