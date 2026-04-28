package it.tecnosystemi.TS.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat$$ExternalSyntheticApiModelOutline0;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener;
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationMessage;
import it.tecnosystemi.TS.R;

public class CustomNotificationListener implements NotificationListener {
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationManager notifManager;

    public void onPushNotificationReceived(Context context, NotificationMessage notificationMessage) {
        this.mContext = context;
        sendNotification(notificationMessage.getData().get("message"));
    }

    public void sendNotification(String str) {
        String substring = str.substring(0, 6);
        Log.d("NOTIFICA", substring);
        try {
            int parseInt = Integer.parseInt(substring);
            Log.d("NOTIFICA", String.valueOf(parseInt));
            if (parseInt == 1) {
                str = this.mContext.getResources().getString(R.string.not_errcom) + " :" + str.substring(6);
            } else if (parseInt == 2) {
                str = this.mContext.getResources().getString(R.string.not_errcom_pico) + " " + str.substring(6);
            }
        } catch (Exception unused) {
        }
        if (this.notifManager == null) {
            this.notifManager = (NotificationManager) this.mContext.getSystemService("notification");
        }
        if (Build.VERSION.SDK_INT >= 26) {
            if (NotificationCompat$$ExternalSyntheticApiModelOutline0.m(this.notifManager, "tecnosystemi_channel_ID") == null) {
                NotificationChannel notificationChannel = new NotificationChannel("tecnosystemi_channel_ID", "tecnosystemi_channel", 4);
                notificationChannel.setDescription("tecnosystemi_channel_notification");
                notificationChannel.enableVibration(true);
                this.notifManager.createNotificationChannel(notificationChannel);
            }
            this.builder = new NotificationCompat.Builder(this.mContext, "tecnosystemi_channel_ID").setColor(this.mContext.getResources().getColor(R.color.redTSColor)).setSmallIcon(R.drawable.logo_ts).setLargeIcon(BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_launcher)).setContentTitle("").setContentText(str).setAutoCancel(true).setDefaults(7);
        } else {
            this.builder = new NotificationCompat.Builder(this.mContext).setColor(this.mContext.getResources().getColor(R.color.redTSColor)).setSmallIcon(R.drawable.logo_ts).setLargeIcon(BitmapFactory.decodeResource(this.mContext.getResources(), R.mipmap.ic_launcher)).setContentTitle("").setContentText(str).setAutoCancel(true).setDefaults(7);
        }
        ((NotificationManager) this.mContext.getSystemService("notification")).notify(1, this.builder.build());
    }
}
