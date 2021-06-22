package org.ahlab.kiwrious.android;

import org.ahlab.kiwrious.android.serial.SerialCommunication;
import org.ahlab.kiwrious.android.service.QueueReader;
import org.ahlab.kiwrious.android.service.QueueWriter;

public class Plugin {

    private static final Plugin ourInstance = new Plugin();

    private float conductivity = 1.2f;
    private float voc = 32;
    private float uv = 2.0f;
    private float lux = 80;
    private float humidity = -70;
    private float temperature = -30;
    private float color_h = 102;
    private float color_s = 102;
    private float color_v = 103;

    private boolean conductivity_online = true;
    private boolean voc_online = true;
    private boolean uv_lux_online = true;
    private boolean humidity_temperature_online = true;
    private boolean color_online = true;


    private SerialCommunication mSerialCommunication;

    public static Plugin getInstance() {
        return ourInstance;
    }

    public Plugin() {
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


    public void StartSerialReader(){
        mSerialCommunication = SerialCommunication.getInstance(Application.getContext());

        QueueWriter queueWriter = new QueueWriter();
        queueWriter.start();

        QueueReader queueReader = new QueueReader(this);
        queueReader.start();
    }

    public void StopSerialReader(){

    }

}
