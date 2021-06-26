package org.ahlab.kiwrious.android.models;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServiceBlockingQueue {
    private static ServiceBlockingQueue instance;
    private static boolean isServiceActive;
    private BlockingQueue serviceQueue;

    private ServiceBlockingQueue() {
        serviceQueue = new ArrayBlockingQueue<byte[]>(60);
    }

    public static ServiceBlockingQueue getInstance() {
        if (instance == null) {
            instance = new ServiceBlockingQueue();
        }
        return instance;
    }

    public BlockingQueue getServiceQueue() {
        return serviceQueue;
    }

    public static void enableQueue() {
        isServiceActive = true;
    }

    public static void disableQueue() {
        isServiceActive = false;
    }

    public static boolean getServiceStatus() {
        return isServiceActive;
    }

    public void destroyQueueExtractor () {
        instance = null;
    }
}
