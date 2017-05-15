package com.example.martin.rgb_catcher.Internet;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.martin.rgb_catcher.R;
import com.example.martin.rgb_catcher.ChooseScrenn_Main;

/**
 * Created by Martin on 19.08.2015.
 */
public class CopyURL_Check extends Service {

    ClipboardManager clipboard;
    boolean done;
    private static int FOREGROUND_SERVICE = 101;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("start_services")) {
            clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    String data = item.getText().toString();
                    StartActivity(data);
                }
            });
            Intent notificationIntent = new Intent(this, CopyURL_Check.class);
            notificationIntent.setAction("stop_services");
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

            Bitmap BIGicon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                    /**zobrazení v notifikáční liště při objevení notifikace*/
                    .setTicker(getString(R.string.Notification_MainTitle))
                    /**Titulek notifikace*/
                    .setContentTitle(getString(R.string.Notification_Title))
                    /**Text notifikace*/
                    .setContentText(getString(R.string.Notification_Text))
                    .setOngoing(true)
                    .setLargeIcon(BIGicon)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent).build();
            /**prehravani zvuku notifikace*/
            try {
                Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification_sound);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startForeground(FOREGROUND_SERVICE, notification);
        }
        else if (intent.getAction().equals("stop_services")) {
            Log.i("Services", "Received Stop Foreground Intent");
            StartActivity(" ");
        }
        return START_STICKY;
    }

    private void StartActivity(String text) {
        done=true;
        Intent intent = new Intent(this, ChooseScrenn_Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("copy", true);
        intent.putExtra("URL", text);
        ChooseScrenn_Main.servicesStop =false;
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!done)
            Toast.makeText(CopyURL_Check.this, R.string.Notification_Error,Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
