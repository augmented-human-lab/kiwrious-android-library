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
    private long resistance = 120;
    private int voc = 32;
    private int co2 = 32;
    private float uv = 2.0f;
    private float lux = 80;
    private float humidity = -70;
    private float temperature = -30;

    private boolean isConductivityOnline = false;
    private boolean isVocOnline = false;
    private boolean isUvOnline = false;
    private boolean isHumidityOnline = false;

    private SerialCommunication mSerialCommunication;
    QueueWriter queueWriter;
    QueueReader queueReader;

    private Plugin() {
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setLux(float lux) {
        this.lux = lux;
    }

    public void setUv(float uv) {
        this.uv = uv;
    }

    public void setConductivity(float conductivity) {
        this.conductivity = conductivity;
    }

    public void setResistance(long resistance) {
        this.resistance = resistance;
    }

    public void setVoc(int voc) {
        this.voc = voc;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public float getConductivity() {
        return conductivity;
    }

    public long getResistance() {
        return resistance;
    }

    public int getVoc() {
        return voc;
    }

    public int getCo2() {
        return co2;
    }

    public float getUV() {
        return uv;
    }

    public float getLux() {
        return lux;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public static Plugin getInstance() {
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

    public void initiateReader() {
        mSerialCommunication = SerialCommunication.getInstance(Application.getContext());
        Application.getContext().registerReceiver(usbConnectivityReceiver, getIntentFilters());
    }


    public boolean startSerialReader() {
        if (mSerialCommunication.isActive() && !isThreadsAlive()) {
            setOnlineSensor(getConnectedSensorName());
            initiateThreads();
            return true;
        }
        return false;
    }

    public String getConnectedSensorName() {
        return mSerialCommunication.getDeviceProductName();
    }

    public void stopSerialReader() {
        mSerialCommunication.stopSerialCommunications();
    }

    private void initiateThreads() {
        ServiceBlockingQueue.enableQueue();
        QueueExtractor.enableQueue();

        queueWriter = new QueueWriter();
        queueReader = new QueueReader(getInstance());

        queueWriter.start();
        queueReader.start();
    }

    private boolean isThreadsAlive() {
        if (!(queueReader == null || queueWriter == null)) {
            return queueReader.isAlive() || queueWriter.isAlive();
        }
        return false;
    }

    private void setOnlineSensor(String deviceName) {
        switch (deviceName) {
            case (Constants.KIWRIOUS_CONDUCTIVITY):
                isConductivityOnline = true;
                isVocOnline = isUvOnline = isHumidityOnline = false;
                break;
            case (Constants.KIWRIOUS_HUMIDITY):
                isHumidityOnline = true;
                isVocOnline = isUvOnline = isConductivityOnline = false;
                break;
            case (Constants.KIWRIOUS_UV):
                isUvOnline = true;
                isVocOnline = isConductivityOnline = isHumidityOnline = false;
                break;
            case (Constants.KIWRIOUS_VOC):
                isVocOnline = true;
                isConductivityOnline = isUvOnline = isHumidityOnline = false;
                break;
            default:
                break;
        }
    }

    private IntentFilter getIntentFilters() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_FTDI_SUCCESS);

        return intentFilter;
    }
}
