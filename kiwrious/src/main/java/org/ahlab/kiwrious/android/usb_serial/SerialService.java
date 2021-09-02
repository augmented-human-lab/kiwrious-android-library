package org.ahlab.kiwrious.android.usb_serial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SerialService {

    private final String TAG = SerialService.class.getName();

    private static SerialService instance;

    private final UsbManager usbManager;
    private UsbDevice usbDevice;

    private boolean isConnected;

    public SerialService(Context context) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(onUsbAttachReceiver, intentFilter);
        context.registerReceiver(onUsbDetachReceiver, intentFilter);

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public static SerialService getInstance(Context context) {
        if (instance == null) {
            instance = new SerialService(context);
        }
        return instance;
    }

    public void initSerialManager() {
        if (!isConnected) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            for (UsbDevice device : deviceList.values()) {
                if (!usbManager.hasPermission(device)) {
                    Log.w(TAG, "No permissions for " + device.getDeviceName());
                } else {
                    Log.w(TAG, "---------------------- Permission Granted -------------------------");
                    usbDevice = device;
                    startCommunications();
                    break;
                }
            }
        }
    }

    private void startCommunications() {
        new Thread(() -> {
            SerialReader serialReader = new SerialReader(usbManager, usbDevice);
            isConnected = serialReader.openConnection();

            ExecutorService executorService = Executors.newFixedThreadPool(1);

            try {
                while (isConnected) {
                    Future<byte[]> readOnce = executorService.submit(serialReader.new Reader());
                    byte[] serialData = readOnce.get();
                    Log.w(TAG, Arrays.toString(serialData));
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            } finally {
                executorService.shutdown();
            }
        }).start();
    }

    private final BroadcastReceiver onUsbAttachReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.w(TAG, "- - - - - - - - - - -  USB ACTION ATTACHED  - - - - - - - - - - - -");
                initSerialManager();
            }
        }
    };

    private final BroadcastReceiver onUsbDetachReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.w(TAG, "- - - - - - - - - - -  USB ACTION DETACHED  - - - - - - - - - - - -");
                isConnected = false;
            }
        }
    };
}
