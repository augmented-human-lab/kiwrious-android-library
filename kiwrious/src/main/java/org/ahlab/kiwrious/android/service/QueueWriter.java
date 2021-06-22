package org.ahlab.kiwrious.android.service;

import android.util.Log;

import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;

import java.util.concurrent.BlockingQueue;

public class QueueWriter extends Thread {

    private final BlockingQueue blockingQueueRx;
    private final BlockingQueue serviceQueue;

    public QueueWriter() {
        QueueExtractor queueExtractor = QueueExtractor.getInstance();
        this.blockingQueueRx = queueExtractor.getQueueRx();

        ServiceBlockingQueue serviceBlockingQueue = ServiceBlockingQueue.getInstance();
        this.serviceQueue = serviceBlockingQueue.getServiceQueue();
    }

    @Override
    public void run() {
        while (ServiceBlockingQueue.getServiceStatus()) {
            int[] eData = new int[9];
            int[] aux;
            try {
                aux = (int[]) blockingQueueRx.take();
                System.arraycopy(aux, 0, eData, 0, eData.length);
                serviceQueue.offer(eData);
            } catch (Exception e) {
                Log.e("Serial Interrupt", "Serial Read Thread Interrupted");
                break;
            }
        }
        super.run();
    }
}
