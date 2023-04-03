package com.example.meetpoint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class  MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + body);

            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "meetpoint_channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }
    public static void sendNotificationToDevice(Context context, String deviceToken, String message) {
        // Set up the notification payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("to", deviceToken);
            JSONObject notification = new JSONObject();
            notification.put("title", "New message from a friend");
            notification.put("body", message);
            payload.put("notification", notification);

            // Send the notification using FCM
            String FCM_API = "https://fcm.googleapis.com/fcm/send";
            String serverKey = "key=AAAAaSn9jvs:APA91bFwYd6g5xT9SpHthehYol7qUhZ3POBtaaClxefP2QBWgfmhHwmMQY7EhmLjnpJ918kZunOucBelLEvJWbX_LnaGI69hLN1xER4zfF_ifLobUZJViS1Zj3lGZBoZZ3U6htFNh2RG";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, payload,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("MyFirebaseMessaging", "Notification sent successfully");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("MyFirebaseMessaging", "Failed to send notification", error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", serverKey);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Add the request to the RequestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

    }
}