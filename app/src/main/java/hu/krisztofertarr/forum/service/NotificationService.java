package hu.krisztofertarr.forum.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import hu.krisztofertarr.forum.ForumApplication;

public class NotificationService {

    public static void notify(Context context, String message) {
        new NotificationHelper(context).notify(message);
    }

    private static class NotificationHelper {

        private static final String CHANNEL_ID = "forum_channel";
        private static final int COLOR = Color.parseColor("#800080");

        private final Context context;
        private final int notificationId;
        private final NotificationManager notificationManager;

        public NotificationHelper(Context context, int notificationId) {
            this.context = context;
            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            this.notificationId = notificationId;

            createChannel();
        }

        public NotificationHelper(Context context) {
            this(context, 0);
        }

        private void createChannel() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return;
            }

            NotificationChannel channel = new NotificationChannel
                    (CHANNEL_ID, "Forum Notification", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(COLOR);
            channel.enableVibration(true);
            channel.setDescription("New notification from Forum");

            notificationManager.createNotificationChannel(channel);
        }

        public void notify(String message) {
            Intent intent = new Intent(context, ForumApplication.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Forum")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(notificationId, builder.build());
        }
    }
}
