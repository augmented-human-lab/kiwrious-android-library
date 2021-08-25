package org.ahlab.kiwrious.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;
import org.ahlab.kiwrious.android.serial.SerialCommunication;
import org.ahlab.kiwrious.android.service.QueueReader;
import org.ahlab.kiwrious.android.service.QueueWriter;
import org.ahlab.kiwrious.android.utils.Constants;

public class Plugin {

    private static Plugin instance;

    private float conductivity = -1.2f;
    private long resistance = -120;
    private int voc = -32;
    private int co2 = -32;
    private int r = -255;
    private int g = -245;
    private int b = -235;
    private float uv = -2.0f;
    private float lux = -80;
    private float humidity = -70;
    private float temperature = -30;
    private int ambientTemperature = -31;
    private int infraredTemperature = -32;
    private int heartRate = -72;

    private boolean isConductivityOnline = false;
    private boolean isVocOnline = false;
    private boolean isUvOnline = false;
    private boolean isHumidityOnline = false;
    private boolean isBodyTempOnline = false;
    private boolean isHeartRateOnline = false;
    private boolean isColorOnline = false;

    private SerialCommunication mSerialCommunication;
    QueueWriter queueWriter;
    QueueReader queueReader;

    private Plugin() {}

    //    ---------------------------------------------------------------------------------------------------------------

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(float temperature) { this.temperature = temperature; }

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

    public void setVoc(int voc) { this.voc = voc; }

    public void setR(int r) { this.r = r; }

    public void setG(int g) { this.g = g; }

    public void setB(int b) { this.b = b; }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public void setAmbientTemperature(int ambientTemperature) { this.ambientTemperature = ambientTemperature; }

    public void setInfraredTemperature(int infraredTemperature){ this.infraredTemperature = infraredTemperature; }

    public void setHeartRate(int heartRate){ this.heartRate = heartRate; }

//    ---------------------------------------------------------------------------------------------------------------

    public float getConductivity() { return conductivity; }

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

    public int getAmbientTemperature() {return ambientTemperature;}

    public int getInfraredTemperature() {return infraredTemperature;}

    public int getHeartRate() {return heartRate;}

    public int getR() {return r;}

    public int getG() {return g;}

    public int getB() {return b;}

    //    ---------------------------------------------------------------------------------------------------------------

    public boolean isHumidityOnline() {
        return isHumidityOnline;
    }

    public boolean isUvOnline() { return isUvOnline; }

    public boolean isConductivityOnline() { return isConductivityOnline; }

    public boolean isVocOnline() { return isVocOnline; }

    public boolean isBodyTempOnline() {return isBodyTempOnline; }

    public boolean isHeartRateOnline() {return isHeartRateOnline; }

    public boolean isColorOnline() {return isColorOnline;}

    //    ---------------------------------------------------------------------------------------------------------------

    public static Plugin getInstance(Context context) {
        if (instance == null) {
            new Application(context);
            instance = new Plugin();
        }
        return instance;
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
        } else if (action.equals(Constants.ACTION_FTDI_FAIL)) {
            isHumidityOnline = isVocOnline = isUvOnline = isConductivityOnline = isBodyTempOnline = isHeartRateOnline = isColorOnline = false;
        }
        }
    };

    public void initiateReader() {
        SerialCommunication.clearInstance();
        mSerialCommunication = SerialCommunication.getInstance(Application.getContext());
        Application.getContext().registerReceiver(usbConnectivityReceiver, getIntentFilters());
    }


    public boolean startSerialReader() {
        initiateReader();
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
                break;
            case (Constants.KIWRIOUS_HUMIDITY):
                isHumidityOnline = true;
                break;
            case (Constants.KIWRIOUS_UV):
                isUvOnline = true;
                break;
            case (Constants.KIWRIOUS_VOC):
                isVocOnline = true;
                break;
            case (Constants.KIWRIOUS_TEMPERATURE):
                isBodyTempOnline = true;
                break;
            case (Constants.KIWRIOUS_HEART_RATE):
                isHeartRateOnline = true;
                break;
            case (Constants.KIWRIOUS_COLOUR):
                isColorOnline = true;
                break;
            default:
                break;
        }
    }

    private IntentFilter getIntentFilters() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_FTDI_SUCCESS);
        intentFilter.addAction(Constants.ACTION_FTDI_FAIL);
        return intentFilter;
    }
}
