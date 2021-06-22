package org.ahlab.kiwrious.android.serial;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueExtractor {
    public static QueueExtractor instanceQueueExtractor;
    private static boolean isQueueEnabled;
    private BlockingQueue queueRx;
    private BlockingQueue queueTx;


    private QueueExtractor () {
        queueRx = new ArrayBlockingQueue<int []>(60);
        queueTx = new ArrayBlockingQueue<byte []>(60);
    }

    public static QueueExtractor getInstance() {
        if (instanceQueueExtractor == null) {
            instanceQueueExtractor = new  QueueExtractor();
        }
        return instanceQueueExtractor;
    }

    public BlockingQueue getQueueTx () {
        return queueTx;
    }

    public BlockingQueue getQueueRx () {
        return queueRx;
    }

    public static void enableQueue() {
        isQueueEnabled = true;
    }

    public static void disableQueue() {
        isQueueEnabled = false;
    }

    public static boolean getQueueStatus() {
        return isQueueEnabled;
    }

    public static void destroyQueueExtractor () {
        instanceQueueExtractor = null;
    }
}
