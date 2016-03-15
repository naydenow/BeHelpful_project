package biz.coddo.behelpful.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import biz.coddo.behelpful.DTOUpdateService;
import biz.coddo.behelpful.R;
import biz.coddo.behelpful.ResponseActivity;

public class ResponseNotification {

    public static final int NOTIFY_ID = 2;

    public ResponseNotification(Context context) {

        if (!ResponseActivity.isResponseActivityStart()) {
            Intent notificationIntent = new Intent(context, ResponseActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Resources res = context.getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setNumber(DTOUpdateService.newResponseNotifyCount)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_email_outline)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                    .setTicker(res.getString(R.string.new_response))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(res.getString(R.string.new_response))
                    .setContentText(res.getString(R.string.go_to_application));

            Notification notification = builder.build();
            notification.defaults = Notification.DEFAULT_ALL;

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, notification);
        }
    }
}
