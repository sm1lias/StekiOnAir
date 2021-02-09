package gr.androidapp.stekionair.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import gr.androidapp.stekionair.MainActivity;

public class OnClearFromRecentService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat.from(this).cancelAll();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        NotificationManagerCompat.from(this).cancelAll();

    }
}
