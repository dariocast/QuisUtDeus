package it.pentagono.app.quisutdeus.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import it.pentagono.app.quisutdeus.R;

/**
 * Created by Dario on 14/12/2017.
 */

public class QUDFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "QuisUtDeus Firebase";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.quisutdeus_logo)
                        .setContentTitle("Quis Ut Deus")
                        .setContentText(remoteMessage.getNotification().getBody());
        Map<String,String> data = remoteMessage.getData();
        if (data.containsKey("tipo") || data.get("tipo").equals("update")) {
            String url = "https://www.dropbox.com/s/5o6xcmxr29o6t7v/quis_ut_deus.apk";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,  PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
        }
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(1, mBuilder.build());
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
