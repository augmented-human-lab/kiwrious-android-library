package org.ahlab.kiwrious.android.tasks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class SensorDecoder {

    public String[] decodeColor(byte[] sensorData) {

        String[] colorValues = new String[3];

        double r = Math.sqrt(ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());
        double g = Math.sqrt(ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());
        double b = Math.sqrt(ByteBuffer.wrap(sensorData, 10, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());

        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV((int) r,(int) g, (int) b, hsv);

        colorValues[0] = String.format(Locale.getDefault(), "%.0f", hsv[0]);
        colorValues[1] = String.format(Locale.getDefault(), "%.0f", hsv[1]*100);
        colorValues[2] = String.format(Locale.getDefault(), "%.0f", hsv[2]*100);

        return colorValues;
    }

    public String[] decodeVOC(byte[] sensorData) {

        String[] vocValues = new String[2];

        int voc = (sensorData[6] & 0xff | (sensorData[7] << 8));
        int co2 = (sensorData[8] & 0xff | (sensorData[7] << 8));

        vocValues[0] = Integer.toString(voc);
        vocValues[1] = Integer.toString(co2);

        return vocValues;
    }

    public String[] decodeConductivity(byte[] sensorData) {

        String[] conductivityValues = new String[2];

        long resistance = (long) (sensorData[6] & 0xff | (sensorData[7] << 8)) * (sensorData[8] & 0xff | (sensorData[9] << 8));

        String uSiemens = "0";
        if (resistance != 0) {
            uSiemens = String.format(Locale.getDefault(), "%.2f", (1 / (float) resistance) * 1000000);
        }

        conductivityValues[0] = Long.toString(resistance);
        conductivityValues[1] = uSiemens;

        return conductivityValues;
    }

    public Float[] decodeHumidity(byte[] sensorData) {

        Float[] humidityValues = new Float[2];

        float humidity = (sensorData[8] & 0xff | (sensorData[9] << 8)) / 100f;
        float temperature = (sensorData[6] & 0xff | (sensorData[7] << 8)) / 100f;

        humidityValues[0] = temperature;
        humidityValues[1] = humidity;

        return humidityValues;
    }

    public String[] decodeUV(byte[] sensorData) {

        String[] lightValues = new String[2];

        float lux = ByteBuffer.wrap(sensorData, 6, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float uv = ByteBuffer.wrap(sensorData, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        lightValues[0] = String.format(Locale.getDefault(), "%.0f", lux);
        lightValues[1] = String.format(Locale.getDefault(), "%.1f", uv);

        return lightValues;
    }

    public String decodeHeartRate(byte[] sensorData) {
        //TODO: decode using byte values
        String heartRateValue = "72";
        return heartRateValue;
    }

    public void decodeSound(Integer... mValues) {
        //TODO: decode using byte values
    }

    public String[] decodeTemperature(byte[] sensorData) {
        String[] temperatureValues = new String[2];

        float ambientTemperature = ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100f;
        float infraredTemperature = ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100f;

        temperatureValues[0] = String.format(Locale.getDefault(), "%.1f", ambientTemperature);
        temperatureValues[1] = String.format(Locale.getDefault(), "%.0f", infraredTemperature);

        return temperatureValues;
    }

    public String[] decodeTemperature2(byte[] sensorData) {
        String[] temperatureValues = new String[2];

        float ambientTemperature = ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100f;
        short x = ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
        float a = ByteBuffer.wrap(sensorData, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float b = ByteBuffer.wrap(sensorData, 14, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float c = ByteBuffer.wrap(sensorData, 18, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        float dTemperature = (float) (Math.round(((a * Math.pow(x, 2)) / Math.pow(10, 5) + b * x + c)));

        temperatureValues[0] = String.format(Locale.getDefault(), "%.0f", ambientTemperature);
        temperatureValues[1] = String.format(Locale.getDefault(), "%.0f", dTemperature);

        return temperatureValues;
    }
}
