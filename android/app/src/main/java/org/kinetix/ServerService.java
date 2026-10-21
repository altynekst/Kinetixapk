package org.kinetix;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class ServerService extends Service {
    private static final int NOTIF_ID = 1;
    private static final java.util.Queue<String> QUEUE = new java.util.LinkedList<>();
    private static boolean running = false;
    private static Context ctx;

    public static void start(Context c, String[] args) {
        ctx = c;
        Intent i = new Intent(c, ServerService.class);
        i.putExtra("args", args);
        c.startForegroundService(i);
        running = true;
    }
    public static void enqueue(String cmd) { synchronized (QUEUE) { QUEUE.add(cmd); } }
    public static boolean isRunning() { return running; }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Notification n = new NotificationCompat.Builder(this, "kinetix")
            .setContentTitle("Kinetix")
            .setContentText("Server running")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .build();
        startForeground(NOTIF_ID, n);
        return START_STICKY;
    }
    @Override public IBinder onBind(Intent i) { return null; }
}
