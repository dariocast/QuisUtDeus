package it.pentagono.app.quisutdeus.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Map;

import it.pentagono.app.quisutdeus.BuildConfig;
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
            FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            // [END get_remote_config_instance]
            // Set default Remote Config parameter values. An app uses the in-app default values, and
            // when you need to adjust those defaults, you set an updated value for only the values you
            // want to change in the Firebase console. See Best Practices in the README for more
            // information.
            // [START set_default_values]
            mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
            String url = mFirebaseRemoteConfig.getString("updated_apk_url");
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
