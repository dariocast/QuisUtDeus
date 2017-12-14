package it.pentagono.app.quisutdeus.firebase;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import it.pentagono.app.quisutdeus.R;

/**
 * Created by Dario on 14/12/2017.
 */

public class QUDFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "QuisUtDeus Firebase";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.quisutdeus_logo)
                        .setContentTitle(remoteMessage.getFrom())
                        .setContentText(remoteMessage.getNotification().getBody());
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
