package com.od.twins.absoftmanager.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.activity.MainActivity;import static com.od.twins.absoftmanager.Constants.MESSAGE;
import static com.od.twins.absoftmanager.Constants.ROOM;
import static com.od.twins.absoftmanager.Constants.NAME;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FirebaseMessService", "onMessageReceived ******");
//        if (remoteMessage.getData() == null || remoteMessage.getData().size() == 0) return;
        final String room = remoteMessage.getData().get(ROOM);
        final String nameUsers = remoteMessage.getData().get(NAME);
        final String message = remoteMessage.getData().get(MESSAGE);
        Log.i("FirebaseMessService", "onMessageReceived:room = " + room);
        Log.i("FirebaseMessService", "onMessageReceived:nameUsers = " + nameUsers);
        Log.i("FirebaseMessService", "onMessageReceived:message = " + message);
        enableDisplay();
        showNotification(room, nameUsers, message);
    }

    @SuppressLint("WakelockTimeout")
    private void enableDisplay() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager != null ? powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WakeLock") : null;
        if (wakeLock != null) {
            wakeLock.acquire();
            wakeLock.release();
        }
    }

    private void showNotification(String room, final String name, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ROOM, room);
        intent.putExtra(NAME, name);
        intent.putExtra(MESSAGE, message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle(name)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(9, notificationBuilder.build());
        }
    }
}
