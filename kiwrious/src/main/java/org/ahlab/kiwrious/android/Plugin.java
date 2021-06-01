package org.ahlab.kiwrious.android;

public class Plugin {

    private static final Plugin ourInstance = new Plugin();

    private float conductivity = 1.2f;
    private float voc = 32;
    private float uv = 2.0f;
    private float lux = 80;
    private float humidity = 70;
    private float temperature = 30;
    private float color_h = 102;
    private float color_s = 102;
    private float color_v = 103;

    private boolean conductivity_online = true;
    private boolean voc_online = true;
    private boolean uv_lux_online = true;
    private boolean humidity_temperature_online = true;
    private boolean color_online = true;

    public static Plugin getInstance() {
        return ourInstance;
    }

    private Plugin(){
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

}
