package biz.coddo.behelpful.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import biz.coddo.behelpful.DTOUpdateService;
import biz.coddo.behelpful.MainActivity;
import biz.coddo.behelpful.R;

public class MarkerNotification {

    private static final String TAG = "MarkerNotification";
    public static final int NOTIFY_ID = 1;

    public MarkerNotification(Context context, int markerID) {

        Log.i(TAG, "create");
        if (!MainActivity.isIsMainActivityStart()) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Resources res = context.getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setNumber(DTOUpdateService.newMarkerNotifyCount)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_map_marker_circle)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                    .setTicker(res.getString(R.string.new_marker_nearby))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(res.getString(R.string.new_marker_nearby));

            if (markerID > 0) {
                builder.setContentText(res.getString(R.string.open_marker));
                notificationIntent.putExtra("MarkerID", markerID);
            } else {
                builder.setContentText(res.getString(R.string.go_to_application));
            }

            Log.i(TAG, "MarkerID" + markerID);

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(contentIntent);

            Notification notification = builder.build();
            notification.defaults = Notification.DEFAULT_ALL;


            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, notification);
        }
    }
}
