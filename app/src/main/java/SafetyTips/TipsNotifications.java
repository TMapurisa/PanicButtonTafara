package SafetyTips;
import com.example.panicbutton.*;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import geofence.NotificationHelper;

public class TipsNotifications {
    private Handler notificationHandler;
    private Runnable sendNotificationRunnable;
    Context context;
    Tips tips = new Tips();
    String msg;

    public void startUse(Context context) {
        this.context = context; // Assign the provided context

        notificationHandler = new Handler(Looper.getMainLooper());

        // Create a Runnable to send the notification
        sendNotificationRunnable = new Runnable() {
            @Override
            public void run() {
                sendNotification(context);
                // Schedule the next notification after 10 seconds
                notificationHandler.postDelayed(this, 10000); // 10000 milliseconds = 10 seconds
                //TODO: change the time to 24hrs since 10 seconds are for testing
            }
        };

        // Start sending notifications
        startSendingNotifications();
    }
    int index;
    private void startSendingNotifications() {
        // Start sending notifications immediately
        notificationHandler.post(sendNotificationRunnable);
    }

    private void sendNotification(Context context) {

        index = (int) (Math.random() * 30) ;
        msg = tips.getMsg(index);
        // Create a NotificationHelper instance
        NotificationHelper notificationHelper = new NotificationHelper(context);

        // Send a notification with your desired title and body
        String notificationTitle = "Safety Tip";
        String notificationBody = msg;
        notificationHelper.sendHighPriorityNotification(notificationTitle, notificationBody, MainActivity.class);
    }
    public void stop(){
        notificationHandler.removeCallbacks(sendNotificationRunnable);
    }

}
