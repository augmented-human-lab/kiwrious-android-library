package org.ahlab.kiwrious.android.utils;

import java.net.PortUnreachableException;

public class Constants {
    public static final int SERVICE_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "KiwriousMonitor";
    public static final String NOTIFICATION_CHANNEL_NAME = "Kiwrious";

    public static final String KIWRIOUS_CONDUCTIVITY = "Kiwrious Conductivity Sensor";
    public static final String KIWRIOUS_HUMIDITY = "Kiwrious Humidity Sensor";
    public static final String KIWRIOUS_UV = "Kiwrious UV Sensor";
    public static final String KIWRIOUS_VOC = "Kiwrious VOC Sensor";
    public static final String KIWRIOUS_TEMPERATURE = "Kiwrious Temperature Sensor";
    public static final String KIWRIOUS_HEART_RATE = "Kiwrious Heart Rate Sensor";
    public static final String KIWRIOUS_COLOUR = "Kiwrious Colour Sensor";
    public static final String KIWRIOUS_SOUND = "Kiwrious Sound Sensor";

    public static final String ACTION_FTDI_SUCCESS = "org.ahlab.kiwrious.android.ACTION_FTDI_SUCCESS";
    public static final String ACTION_FTDI_FAIL = "org.ahlab.kiwrious.android.ACTION_FTDI_FAIL";

    public static final String STRINGS_START_RECORD = "Start Recording";
    public static final String STRINGS_STOP_RECORD = "Stop Recording";

    public static final int KIWRIOUS_SERIAL_FRAME_SIZE_TX = 16;
    public static final int KIWRIOUS_SERIAL_FRAME_SIZE_RX = 9;


    public static final int KIWRIOUS_SENSOR_TYPE = KIWRIOUS_SERIAL_FRAME_SIZE_RX - 1;

    public static final int SENSOR_COLOUR = 0;
    public static final int SENSOR_CONDUCTIVITY = 1;
    public static final int SENSOR_HEART_RATE = 2;
    public static final int SENSOR_HUMIDITY = 3;
    public static final int SENSOR_SOUND = 4;
    public static final int SENSOR_TEMPERATURE = 5;
    public static final int SENSOR_UV = 6;
    public static final int SENSOR_VOC = 7;
    public static final int SENSOR_TEMPERATURE2 = 8;
}
