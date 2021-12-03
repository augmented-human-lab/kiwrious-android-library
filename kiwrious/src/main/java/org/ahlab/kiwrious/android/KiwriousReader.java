package org.ahlab.kiwrious.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.usb_serial.QueueExtractor;
import org.ahlab.kiwrious.android.service.QueueReader;
import org.ahlab.kiwrious.android.service.QueueWriter;
import org.ahlab.kiwrious.android.usb_serial.SerialService;
import org.ahlab.kiwrious.android.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KiwriousReader {
    private static KiwriousReader instance;

    private float conductivity = 0;
    private long resistance = 0;
    private int voc = 0;
    private int co2 = 0;
    private int r = 0;
    private int g = 0;
    private int b = 0;
    private float uv = 0;
    private long lux = 0;
    private float humidity = 0;
    private float temperature = 0;
    private int ambientTemperature = 0;
    private int infraredTemperature = 0;
    private int heartRate = 0;

    private byte[] rawValues = new byte[26];

    private SerialService serialService;

    QueueWriter queueWriter;
    QueueReader queueReader;

    public KiwriousCallback SensorConnected;

    private HashMap<String, Boolean> sensorStatus = new HashMap<>();

    private String[] supportedSensors = {
            Constants.KIWRIOUS_COLOUR, 
            Constants.KIWRIOUS_HEART_RATE, 
            Constants.KIWRIOUS_CONDUCTIVITY, 
            Constants.KIWRIOUS_HUMIDITY, 
            Constants.KIWRIOUS_TEMPERATURE, 
            Constants.KIWRIOUS_UV, 
            Constants.KIWRIOUS_UV2,
            Constants.KIWRIOUS_VOC
    };

    private KiwriousReader() {
        for (String sensorName : supportedSensors)
        {
           sensorStatus.put(sensorName, false);
        }

    }

    //    ---------------------------------------------------------------------------------------------------------------

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setLux(long lux) {
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

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public void setAmbientTemperature(int ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public void setInfraredTemperature(int infraredTemperature) {
        this.infraredTemperature = infraredTemperature;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public void setRawValues(byte[] rawValues){
        this.rawValues = rawValues;
    }

//    ---------------------------------------------------------------------------------------------------------------

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

    public long getLux() {
        return lux;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getAmbientTemperature() {
        return ambientTemperature;
    }

    public int getInfraredTemperature() {
        return infraredTemperature;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public byte[] getRawValues() {return rawValues; }

    //    ---------------------------------------------------------------------------------------------------------------

    public boolean isHumidityOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_HUMIDITY);
    }

    public boolean isUvOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_UV) || sensorStatus.get(Constants.KIWRIOUS_UV2);
    }

    public boolean isConductivityOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_CONDUCTIVITY);
    }

    public boolean isVocOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_VOC);
    }

    public boolean isBodyTempOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_TEMPERATURE);
    }

    public boolean isHeartRateOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_HEART_RATE);
    }

    public boolean isColorOnline() {
        return sensorStatus.get(Constants.KIWRIOUS_COLOUR);
    }

    //    ---------------------------------------------------------------------------------------------------------------

    public static KiwriousReader getInstance(Context context) {
        if (instance == null) {
            new Application(context);
            instance = new KiwriousReader();
        }
        return instance;
    }

    public static KiwriousReader getInstance() {
        if (instance == null) {
            instance = new KiwriousReader();
        }
        return instance;
    }

    private final BroadcastReceiver usbConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_FTDI_SUCCESS)) { // usb device connected
                setOnlineSensor(getConnectedSensorName());
                initiateThreads();
            } else if (action.equals(Constants.ACTION_FTDI_FAIL)) { // usb device removed
                for(Map.Entry<String, Boolean> entry : sensorStatus.entrySet()) {
                    String sensorName = entry.getKey();
                    sensorStatus.put(sensorName, false);
                }
            }
        }
    };

    public void initiateReader() {
        serialService = SerialService.getInstance(Application.getContext());
        serialService.initSerialManager(Application.getContext());
        Application.getContext().registerReceiver(usbConnectivityReceiver, getIntentFilters());
    }


    public boolean startSerialReader() {
        initiateReader();
        if (serialService.isConnected() && !isThreadsAlive()) {
            setOnlineSensor(getConnectedSensorName());
            initiateThreads();
            return true;
        }
        return false;
    }

    public String getConnectedSensorName() {
        return serialService.getDeviceName();
    }

    public void stopSerialReader() {
        serialService.stopCommunications(Application.getContext());
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
        boolean isKiwriousDevice = Arrays.asList(supportedSensors).contains(deviceName);
        if(isKiwriousDevice){
            sensorStatus.put(deviceName, true);
        }
    }

    private IntentFilter getIntentFilters() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_FTDI_SUCCESS);
        intentFilter.addAction(Constants.ACTION_FTDI_FAIL);
        return intentFilter;
    }
}
