package org.ahlab.kiwrious.android.usb_serial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.utils.Constants;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SerialService {

    private final String TAG = SerialService.class.getName();

    private static SerialService instance;

    private final UsbManager usbManager;
    private UsbDevice usbDevice;

    private final BlockingQueue<byte[]> blockingQueueRx;

    private boolean isConnected;

    public SerialService(Context context) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(onUsbAttachReceiver, intentFilter);
        context.registerReceiver(onUsbDetachReceiver, intentFilter);

        QueueExtractor queueExtractor = QueueExtractor.getInstance();
        blockingQueueRx = queueExtractor.getQueueRx();

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public static SerialService getInstance(Context context) {
        if (instance == null) {
            instance = new SerialService(context);
        }
        return instance;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getDeviceName() {
        if(isConnected){
            return usbDevice.getProductName();
        } else {
            return "";
        }
    }

    public void initSerialManager(Context context) {
        if (!isConnected) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            for (UsbDevice device : deviceList.values()) {
                if (!usbManager.hasPermission(device)) {
                    Log.w(TAG, "No permissions for " + device.getDeviceName());
                } else {
                    Log.w(TAG, "---------------------- Permission Granted -------------------------");
                    usbDevice = device;
                    startCommunications(context);
                    break;
                }
            }
        }
    }

    public void stopCommunications(Context context) {
        isConnected = false;

        ServiceBlockingQueue.disableQueue();
        QueueExtractor.disableQueue();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION_FTDI_FAIL);
        context.sendBroadcast(broadcastIntent);
    }

    private void startCommunications(Context context) {
        Thread thread = new Thread() {
            public void run() {
                SerialReader serialReader = new SerialReader(usbManager, usbDevice);
                isConnected = serialReader.openConnection();

                QueueExtractor.enableQueue();
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_FTDI_SUCCESS);
                context.sendBroadcast(intent);

                ExecutorService executorService = Executors.newFixedThreadPool(1);

                try {
                    while (isConnected) {
                        Future<byte[]> readOnce = executorService.submit(serialReader.new Reader());
                        byte[] serialData = readOnce.get();
                        if (blockingQueueRx != null) {
                            boolean isCapacityAvailable = blockingQueueRx.offer(serialData);
                            if (!isCapacityAvailable) blockingQueueRx.clear();
                        }
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    executorService.shutdown();
                }
            }
        };
        thread.start();
    }

    private final BroadcastReceiver onUsbAttachReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.w(TAG, "- - - - - - - - - - -  USB ACTION ATTACHED  - - - - - - - - - - - -");
                initSerialManager(context);
            }
        }
    };

    private final BroadcastReceiver onUsbDetachReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.w(TAG, "- - - - - - - - - - -  USB ACTION DETACHED  - - - - - - - - - - - -");
                stopCommunications(context);
            }
        }
    };
}
