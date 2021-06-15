package org.ahlab.kiwrious.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

//import org.ahlab.kiwrious.android.MainActivity;
import org.ahlab.kiwrious.android.R;
import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;
import org.ahlab.kiwrious.android.utils.Constants;

import java.util.concurrent.BlockingQueue;

public class DataHandlerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        QueueReader queueReader = new QueueReader();
//        queueReader.start();
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        Notification notification =
//                new Notification.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
//                        .setContentTitle(getText(R.string.app_name))
//                        .setContentText(getText(R.string.app_name))
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .build();
//
//        startForeground(Constants.SERVICE_ID, notification);
        return START_STICKY;
    }

    public static class QueueReader extends Thread {

        private final BlockingQueue blockingQueueRx;
        private final BlockingQueue serviceQueue;

        private QueueReader() {
            QueueExtractor queueExtractor = QueueExtractor.getInstance();
            this.blockingQueueRx = queueExtractor.getQueueRx();

            ServiceBlockingQueue serviceBlockingQueue = ServiceBlockingQueue.getInstance();
            this.serviceQueue = serviceBlockingQueue.getServiceQueue();
        }

        @Override
        public void run() {
            ServiceBlockingQueue.enableQueue();
            while (ServiceBlockingQueue.getServiceStatus()) {
                byte[] eData = new byte[4];
                byte[] aux;
                try {
                    aux = (byte[]) blockingQueueRx.take();
                    System.arraycopy(aux, 0, eData, 0, eData.length);
                    serviceQueue.offer(eData);
                } catch (Exception e) {
                    Log.e("Interrupt", "Service Read Thread Interrupted");
                    break;
                }
            }
            super.run();
        }
    }
}
