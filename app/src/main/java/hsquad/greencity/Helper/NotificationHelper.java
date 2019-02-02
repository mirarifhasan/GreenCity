package hsquad.greencity.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import hsquad.greencity.R;


public class NotificationHelper extends ContextWrapper {

    private static final String GC_CHANEL_ID = "hsquad.greencity.GCDev";
    private static final String GC_CHANEL_NAME = "GC";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //Only working this function if API is 26 or higher
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel gcChanel = new NotificationChannel(GC_CHANEL_ID, GC_CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        gcChanel.enableLights(false);
        gcChanel.enableVibration(true);
        gcChanel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(gcChanel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getGreenCityChannelNotification(String title, String body, PendingIntent contentIntent,
                                                                            Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(), GC_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri).setAutoCancel(false);
    }
}
