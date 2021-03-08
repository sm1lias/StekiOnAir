package gr.androidapp.stekionair;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import gr.androidapp.stekionair.Services.NotificationActionService;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class CustomNotification {

    public static final String CHANNEL_ID="channel1";
    public static final String ACTION_PLAY="actionplay";
    public static final String ACTION_CLOSE="actionclose";
    public static Notification notification;



    public static void customNotification(Context context, String data,String title, int playbutton) {
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat=new MediaSessionCompat(context, "TAG");

        Intent intentPlay=new Intent(context, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay=PendingIntent.getBroadcast(context, 0, intentPlay,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentClose=new Intent(context, NotificationActionService.class)
                .setAction(ACTION_CLOSE);
        PendingIntent pendingIntentClose=PendingIntent.getBroadcast(context, 0, intentClose,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentOpen= new Intent(context,MainActivity.class);
        intentOpen.setAction(Intent.ACTION_MAIN);
        intentOpen.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntentOpen = PendingIntent.getActivity(context, 0, intentOpen, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setContentTitle(title)
                    .setContentText(data)
                    .setContentIntent(pendingIntentOpen)
                    .addAction(playbutton,"Play", pendingIntentPlay)
                    .addAction(R.drawable.ic_close,"Close", pendingIntentClose)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

        }
        else{
            notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setContentTitle("StekiOnAir")
                    .setContentText(data)
                    .setContentIntent(pendingIntentOpen)
                    .addAction(playbutton,"Play", pendingIntentPlay)
                    .addAction(R.drawable.ic_close,"Close", pendingIntentClose)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
        }
        notificationManagerCompat.notify(1,notification);
    }
}
