package it.tecnosystemi.TS.Commands;

import android.app.Activity;
import android.content.Context;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import androidx.core.internal.view.SupportMenu;
import it.tecnosystemi.TS.Activity.BootloaderActivity;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MySocketBootLoader {
    public static final int BUFFER_SIZE = 2048;
    public static final int SOCKET_TIMEOUT = 10000;
    public static final int SOCKET_TIMES = 5;
    /* access modifiers changed from: private */
    public static String TAG = "bootloader";
    public static final char bootverCommand = '5';
    public static final char cleanFlashCommand = '0';
    static boolean doneRecovery = false;
    private static final char endChar = '\r';
    public static final char eraseFlashCommand = '6';
    private static final char error = '\b';
    public static final char fwVerCommand = '\u000f';
    private static final char header_size = '\u0005';
    protected static final char[] hexArray = {cleanFlashCommand, writeIDCommand, readCRCCommand, '3', rebootCommand, bootverCommand, eraseFlashCommand, '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final char hwVerCommand = ':';
    private static MySocketBootLoader instance = null;
    public static int lastFWPK = 0;
    public static int lastcrc = 0;
    static boolean lesstimeout = false;
    public static Context mcon = null;
    private static final char ok = '\t';
    public static final char readCRCCommand = '2';
    public static final char rebootCommand = '4';
    public static List<byte[]> sentmsg = null;
    public static final char serialCommand = '$';
    /* access modifiers changed from: private */
    public static Socket socket = null;
    private static final char startChar = '*';
    public static Network tobindnet = null;
    public static final char writeIDCommand = '1';

    private MySocketBootLoader() {
    }

    public static Socket connectWithSocket(String str, int i) throws Exception {
        Network network;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(str), i);
        Socket socket2 = new Socket();
        if (Build.VERSION.SDK_INT >= 21 && (network = tobindnet) != null) {
            try {
                network.bindSocket(socket2);
            } catch (Exception unused) {
            }
        }
        if (lesstimeout) {
            lesstimeout = false;
            socket2.connect(inetSocketAddress, 1000);
        } else {
            socket2.connect(inetSocketAddress, 10000);
        }
        socket2.setSoTimeout(10000);
        return socket2;
    }

    public static void disconnectWithSocket() {
        disconnectWithSocket(socket);
    }

    public static void disconnectWithSocket(Socket socket2) {
        if (socket2 != null && socket2.isConnected()) {
            try {
                socket2.close();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    static class MySocketReadAndWrite extends AsyncTask<String, Void, String> {
        MySocketReadAndWrite() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... strArr) {
            int i;
            byte b;
            try {
                i = Integer.valueOf(strArr[3]).intValue();
            } catch (Exception unused) {
                i = 5;
            }
            byte[] hexStringToByteArray = MySocketBootLoader.hexStringToByteArray(strArr[0]);
            byte[] bArr = null;
            boolean z = true;
            while (i > 0 && z) {
                i--;
                try {
                    if (MySocketBootLoader.socket == null || !MySocketBootLoader.socket.isConnected()) {
                        Socket unused2 = MySocketBootLoader.socket = MySocketBootLoader.connectWithSocket(strArr[1], Integer.parseInt(strArr[2]));
                    }
                    if (MySocketBootLoader.socket != null && MySocketBootLoader.socket.isConnected()) {
                        MySocketBootLoader.sendDataToSocket(hexStringToByteArray, MySocketBootLoader.socket);
                        byte[] bArr2 = new byte[2048];
                        bArr = Arrays.copyOfRange(bArr2, 0, MySocketBootLoader.socket.getInputStream().read(bArr2));
                        if (bArr != null && bArr.length > 2 && bArr[0] != 65 && (!((b = bArr[2]) == 49 || b == 48 || b == 52 || b == 54) || bArr[3] == 9)) {
                            z = false;
                        }
                        Log.e(MySocketBootLoader.TAG, "#### READ: " + MySocketBootLoader.printByteArray(bArr));
                    }
                } catch (Exception e) {
                    if (hexStringToByteArray.length > 2 && hexStringToByteArray[2] != 49) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception unused3) {
                        }
                    }
                    Log.e(MySocketBootLoader.TAG, "TENTATIVO " + i + " EXCEPTION " + e.toString() + " CMD: " + strArr[0]);
                    Socket unused4 = MySocketBootLoader.socket = null;
                    bArr = null;
                }
            }
            if (bArr == null || (hexStringToByteArray.length > 2 && hexStringToByteArray[2] == 52)) {
                MySocketBootLoader.disconnectWithSocket(MySocketBootLoader.socket);
            }
            return MySocketBootLoader.printByteArray(bArr);
        }
    }

    public static void sendDataToSocket(byte[] bArr, Socket socket2) throws IOException {
        if (bArr != null && socket2.isConnected()) {
            OutputStream outputStream = socket2.getOutputStream();
            outputStream.write(bArr);
            outputStream.flush();
        }
    }

    public static String sendAndReceive(byte[] bArr) {
        try {
            MySocketReadAndWrite mySocketReadAndWrite = new MySocketReadAndWrite();
            Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
            String printByteArray = printByteArray(bArr);
            String str = Constants.ip;
            return (String) mySocketReadAndWrite.executeOnExecutor(executor, new String[]{printByteArray, str, Constants.port + "", "5"}).get();
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
            return "";
        } catch (ExecutionException e2) {
            Log.d(TAG, e2.toString());
            return "";
        }
    }

    public static byte[] prepareDatagram(char c, byte[] bArr, char c2) {
        bArr[0] = 42;
        bArr[1] = (byte) c2;
        bArr[2] = (byte) c;
        bArr[c2 - 1] = 13;
        return bArr;
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

    public static byte[] hexStringToByteArray(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static byte[] write(char c) {
        return prepareDatagram(c, new byte[4], 4);
    }

    public static byte[] writeCommandForChunk(char c, byte[] bArr) {
        int length = bArr.length + 4;
        byte[] bArr2 = new byte[length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i + 3] = bArr[i];
        }
        return prepareDatagram(c, bArr2, (char) length);
    }

    public static boolean cleanFlash(BootloaderActivity bootloaderActivity, boolean z) {
        byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(cleanFlashCommand)));
        if (hexStringToByteArray == null || hexStringToByteArray.length <= 4 || hexStringToByteArray[3] != 9) {
            return false;
        }
        lastFWPK = 0;
        sentmsg = new ArrayList();
        return writeFW(bootloaderActivity, z);
    }

    public static byte[] getBootVer(BootloaderActivity bootloaderActivity) {
        try {
            return hexStringToByteArray(sendAndReceive(write(bootverCommand)));
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] getHwVer(BootloaderActivity bootloaderActivity) {
        lesstimeout = true;
        try {
            byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(hwVerCommand)));
            lesstimeout = false;
            return hexStringToByteArray;
        } catch (Exception unused) {
            lesstimeout = false;
            return null;
        }
    }

    public static byte[] getFwVer(BootloaderActivity bootloaderActivity) {
        try {
            return hexStringToByteArray(sendAndReceive(write(fwVerCommand)));
        } catch (Exception unused) {
            return null;
        }
    }

    public static boolean writeFW(final BootloaderActivity bootloaderActivity, boolean z) {
        bootloaderActivity.runOnUiThread(new Runnable() {
            public void run() {
                BootloaderActivity.this.startWrite();
            }
        });
        DataClass instance2 = DataClass.getInstance(mcon);
        int size = instance2.firmware_chunk_list.size();
        int i = lastFWPK;
        int i2 = i < 0 ? 0 : i;
        if (i > 0) {
            i2++;
        }
        loop0:
        while (true) {
            int i3 = 5;
            while (instance2.firmware_chunk_list != null && size > i2) {
                byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(writeCommandForChunk(writeIDCommand, instance2.firmware_chunk_list.get(i2))));
                if (hexStringToByteArray != null && hexStringToByteArray.length > 4 && hexStringToByteArray[3] == 9) {
                    lastFWPK = i2;
                    sentmsg.add(instance2.firmware_chunk_list.get(i2));
                    i2++;
                    if (i2 == size) {
                        lastFWPK = 0;
                        bootloaderActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.startCrc();
                            }
                        });
                    } else if (i2 % (size / 100) == 0) {
                        double d = (double) i2;
                        double d2 = (double) size;
                        Double.isNaN(d);
                        Double.isNaN(d2);
                        bootloaderActivity.setProgressBar((int) ((d / d2) * 100.0d));
                    }
                } else if (i3 == 0) {
                    return false;
                } else {
                    i3--;
                }
            }
        }
        return readCRC(bootloaderActivity, z);
    }

    public static boolean readCRC(final BootloaderActivity bootloaderActivity, boolean z) {
        byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(readCRCCommand)));
        if (hexStringToByteArray == null || hexStringToByteArray.length <= 5) {
            return false;
        }
        if (!computeCRCFirmware((hexStringToByteArray[3] & 255) | ((hexStringToByteArray[4] & 255) << 8), bootloaderActivity)) {
            return false;
        }
        if (!z) {
            return reboot(bootloaderActivity);
        }
        bootloaderActivity.runOnUiThread(new Runnable() {
            public void run() {
                BootloaderActivity.this.startEraseFlash();
            }
        });
        return eraseFlash(bootloaderActivity);
    }

    public static boolean readCRCForControllo(BootloaderActivity bootloaderActivity) {
        byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(readCRCCommand)));
        if (hexStringToByteArray == null || hexStringToByteArray.length <= 5) {
            return false;
        }
        byte b = hexStringToByteArray[4];
        byte b2 = hexStringToByteArray[3];
        lastcrc = computeCRCFirmwareSent();
        return false;
    }

    public static boolean readCRCForRetry(BootloaderActivity bootloaderActivity, boolean z) {
        byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(readCRCCommand)));
        doneRecovery = true;
        if (hexStringToByteArray == null || hexStringToByteArray.length <= 5) {
            return false;
        }
        byte b = (hexStringToByteArray[3] & 255) | ((hexStringToByteArray[4] & 255) << 8);
        lastcrc = computeCRCFirmwareSent();
        Log.d(TAG, "readCRCForRetry MY CRC: " + lastcrc + " - MATTEO CRC: " + b);
        DataClass instance2 = DataClass.getInstance(mcon);
        if (b != lastcrc) {
            lastFWPK++;
            sentmsg.add(instance2.firmware_chunk_list.get(lastFWPK));
            int computeCRCFirmwareSent = computeCRCFirmwareSent();
            lastcrc = computeCRCFirmwareSent;
            if (computeCRCFirmwareSent != b) {
                lastFWPK = 0;
                return false;
            }
            Log.d(TAG, "readCRCForRetry MY CRC: " + lastcrc + " - MATTEO CRC: " + b);
        }
        return writeFW(bootloaderActivity, z);
    }

    public static boolean eraseFlash(BootloaderActivity bootloaderActivity) {
        try {
            byte[] hexStringToByteArray = hexStringToByteArray(sendAndReceive(write(eraseFlashCommand)));
            if (hexStringToByteArray != null && hexStringToByteArray.length > 4 && hexStringToByteArray[3] == 9) {
                return reboot(bootloaderActivity);
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public static boolean reboot(BootloaderActivity bootloaderActivity) {
        try {
            lesstimeout = true;
            hexStringToByteArray(sendAndReceive(write(rebootCommand)));
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean computeCRCFirmware(int i, Activity activity) {
        DataClass.getInstance(mcon);
        int i2 = SupportMenu.USER_MASK;
        for (int i3 = 0; i3 < sentmsg.size(); i3++) {
            byte[] bArr = sentmsg.get(i3);
            for (int i4 = 4; i4 < sentmsg.get(i3).length; i4++) {
                byte b = bArr[i4];
                for (int i5 = 0; i5 < 8; i5++) {
                    boolean z = ((b >> (7 - i5)) & 1) == 1;
                    boolean z2 = ((i2 >> 15) & 1) == 1;
                    i2 <<= 1;
                    if (z ^ z2) {
                        i2 ^= 4129;
                    }
                }
                i2 &= SupportMenu.USER_MASK;
            }
        }
        String str = TAG;
        Log.d(str, "NEW MY CRC: " + i2 + " - MATTEO CRC: " + i);
        if (i == i2) {
            return true;
        }
        return false;
    }

    public static boolean computeCRCFirmwareOLD(int i) {
        DataClass instance2 = DataClass.getInstance(mcon);
        int i2 = SupportMenu.USER_MASK;
        for (int i3 = 0; i3 < instance2.firmware_chunk_list.size(); i3++) {
            byte[] bArr = instance2.firmware_chunk_list.get(i3);
            for (int i4 = 4; i4 < instance2.firmware_chunk_list.get(i3).length; i4++) {
                byte b = bArr[i4];
                for (int i5 = 0; i5 < 8; i5++) {
                    boolean z = ((b >> (7 - i5)) & 1) == 1;
                    boolean z2 = ((i2 >> 15) & 1) == 1;
                    i2 <<= 1;
                    if (z ^ z2) {
                        i2 ^= 4129;
                    }
                }
                i2 &= SupportMenu.USER_MASK;
            }
        }
        String str = TAG;
        Log.d(str, "OLD MY CRC: " + i2 + " - MATTEO CRC: " + i);
        if (i == i2) {
            return true;
        }
        return false;
    }

    public static int computeCRCFirmwareSent() {
        DataClass.getInstance(mcon);
        int i = SupportMenu.USER_MASK;
        for (int i2 = 0; i2 < sentmsg.size(); i2++) {
            byte[] bArr = sentmsg.get(i2);
            for (int i3 = 4; i3 < sentmsg.get(i2).length; i3++) {
                byte b = bArr[i3];
                for (int i4 = 0; i4 < 8; i4++) {
                    boolean z = true;
                    boolean z2 = ((b >> (7 - i4)) & 1) == 1;
                    if (((i >> 15) & 1) != 1) {
                        z = false;
                    }
                    i <<= 1;
                    if (z2 ^ z) {
                        i ^= 4129;
                    }
                }
                i &= SupportMenu.USER_MASK;
            }
        }
        return i;
    }
}
