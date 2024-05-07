package hu.krisztofertarr.forum.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.util.Callback;

public class BroadcastService {

    private static final long INTERVAL = 1000 * 60;

    public BroadcastService(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                INTERVAL,
                pendingIntent
        );
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final ForumService forumService = ForumService.getInstance();

            forumService.randomThread(new Callback<Thread>() {
                @Override
                public void onSuccess(Thread data) {
                    if (data == null) {
                        return;
                    }
                    new NotificationService.NotificationBuilder()
                            .title("Új téma")
                            .message("Szólj hozzá a(z) " + data.getTitle() + " témához!")
                            .action(new NotificationCompat.Action(
                                    R.drawable.note_sticky_solid,
                                    "Megnyitás",
                                    NotificationService.createPendingIntent(context, data.getId())
                            ))
                            .send(context);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("BroadcastService", "Failed to get random thread", e);
                }
            });
        }
    }
}
