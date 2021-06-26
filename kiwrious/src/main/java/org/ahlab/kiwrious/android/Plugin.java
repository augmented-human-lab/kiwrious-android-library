package org.ahlab.kiwrious.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;
import org.ahlab.kiwrious.android.serial.SerialCommunication;
import org.ahlab.kiwrious.android.service.QueueReader;
import org.ahlab.kiwrious.android.service.QueueWriter;
import org.ahlab.kiwrious.android.utils.Constants;

public class Plugin {

    private static Plugin instance;

    private float conductivity = 1.2f;
    private float voc = 32;
    private float uv = 2.0f;
    private float lux = 80;
    private float humidity = -70;
    private float temperature = -30;
    private float color_h = 102;
    private float color_s = 102;
    private float color_v = 103;

    private boolean conductivity_online = false;
    private boolean voc_online = false;
    private boolean uv_lux_online = false;
    private boolean humidity_temperature_online = false;
    private boolean color_online = false;


    private SerialCommunication mSerialCommunication;
    QueueWriter queueWriter;
    QueueReader queueReader;

    private Plugin () {
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getConductivity(){
        return conductivity;
    }

    public float getVoc(){
        return voc;
    }

    public float getUV(){
        return uv;
    }

    public float getLux(){
        return lux;
    }

    public float getHumidity(){
        return humidity;
    }

    public float getTemperature(){
        return temperature;
    }

    public float getColorH(){
        return color_h;
    }

    public float getColorS(){
        return color_s;
    }

    public float getColorV(){
        return color_v;
    }

    public static Plugin getInstance () {
        if (instance == null) {
            instance = new Plugin();
        }
        return instance;
    }

    private final BroadcastReceiver usbConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_FTDI_SUCCESS)) {
                setOnlineSensor(getConnectedSensorName());
                initiateThreads();
            }
        }
    };

    public void initiateReader(){
        mSerialCommunication = SerialCommunication.getInstance(Application.getContext());
        Application.getContext().registerReceiver(usbConnectivityReceiver, getIntentFilters());
    }


    public boolean startSerialReader () {
        if (mSerialCommunication.isActive() && !isThreadsAlive()) {
            setOnlineSensor(getConnectedSensorName());
            initiateThreads();
            return true;
        }
        return false;
    }

    public String getConnectedSensorName () {
        return mSerialCommunication.getDeviceProductName();
    }

    public void stopSerialReader() {
        mSerialCommunication.stopSerialCommunications();
    }

    private void initiateThreads () {
        ServiceBlockingQueue.enableQueue();
        QueueExtractor.enableQueue();

        queueWriter = new QueueWriter();
        queueReader = new QueueReader(getInstance());

        queueWriter.start();
        queueReader.start();
    }

    private boolean isThreadsAlive () {
        if (!(queueReader == null || queueWriter == null)) {
            return queueReader.isAlive() || queueWriter.isAlive();
        }
        return false;
    }

    private void setOnlineSensor(String deviceName) {
        switch (deviceName) {
            case (Constants.KIWRIOUS_CONDUCTIVITY):
                conductivity_online = true;
                voc_online = uv_lux_online = humidity_temperature_online = color_online = false;
                break;
            case (Constants.KIWRIOUS_HUMIDITY):
                humidity_temperature_online = true;
                voc_online = uv_lux_online = conductivity_online = color_online = false;
                break;
            case (Constants.KIWRIOUS_UV):
                uv_lux_online = true;
                voc_online = conductivity_online = humidity_temperature_online = color_online = false;
                break;
            case (Constants.KIWRIOUS_COLOUR):
                color_online = true;
                voc_online = uv_lux_online = humidity_temperature_online = conductivity_online = false;
                break;
            case (Constants.KIWRIOUS_VOC):
                voc_online = true;
                conductivity_online = uv_lux_online = humidity_temperature_online = color_online = false;
                break;
            default:
                break;
        }
    }

    private IntentFilter getIntentFilters()
    {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_FTDI_SUCCESS);

        return intentFilter;
    }
}
