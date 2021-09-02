package org.ahlab.kiwrious.android.usb_serial;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.concurrent.Callable;

public class SerialReader {

    private final String TAG = SerialReader.class.getName();

    private final UsbManager usbManager;
    private final UsbDevice usbDevice;

    private UsbEndpoint endpoint;
    private UsbDeviceConnection connection;

    private static final int TIMEOUT = 0;
    private boolean forceClaim = true;

    public SerialReader(UsbManager usbManager, UsbDevice usbDevice) {
        this.usbManager = usbManager;
        this.usbDevice = usbDevice;
    }

    public boolean openConnection() {
        UsbInterface usbInterface = usbDevice.getInterface(1);
        endpoint = usbInterface.getEndpoint(1);
        connection = usbManager.openDevice(usbDevice);
        return connection.claimInterface(usbInterface, forceClaim);
    }

    public class Reader implements Callable<byte[]> {

        public byte[] call() {
            final byte[] bytes = new byte[26];
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
            return bytes.clone();
        }
    }
}
