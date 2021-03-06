package org.ahlab.kiwrious.android.utils;

public class Constants {
    public static final int SERVICE_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "KiwriousMonitor";
    public static final String NOTIFICATION_CHANNEL_NAME = "Kiwrious";

    public static final String KIWRIOUS_CONDUCTIVITY = "Kiwrious Conductance Sensor";
    public static final String KIWRIOUS_HUMIDITY = "Kiwrious Humidity Sensor";
    public static final String KIWRIOUS_UV = "Kiwrious UV Sensor";
    public static final String KIWRIOUS_UV2 = "Kiwrious UV and Light Sensor";
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
    public static final int KIWRIOUS_SERIAL_FRAME_SIZE_RX = 26;

    public static final int KIWRIOUS_SENSOR_TYPE = 2;

    public static final int SENSOR_UV = 1;
    public static final int SENSOR_CONDUCTIVITY = 4;
    public static final int SENSOR_HEART_RATE = 5;
    public static final int SENSOR_VOC = 6;
    public static final int SENSOR_HUMIDITY = 7;
    public static final int SENSOR_TEMPERATURE2 = 9;
    public static final int SENSOR_COLOUR = 3;
    public static final int SENSOR_SOUND = -1;
    public static final int SENSOR_TEMPERATURE = 2;
}
