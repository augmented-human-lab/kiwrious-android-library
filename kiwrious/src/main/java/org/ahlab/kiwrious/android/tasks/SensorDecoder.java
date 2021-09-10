package org.ahlab.kiwrious.android.tasks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class SensorDecoder {

    public String[] decodeDefaultValues(Integer... mValues) {
        //TODO: decode using byte values

        String[] defaultValues = new String[mValues.length];
        for (int i = 0; i < mValues.length; i++) {
            defaultValues[i] = String.format(Locale.getDefault(), "%d", mValues[i]);
        }
        return defaultValues;
    }

    public String[] decodeColor(Integer... mValues) {
        //TODO: decode using byte values
        String[] colorValues = new String[3];
        colorValues[0] = String.format(Locale.getDefault(), "%d", mValues[0] / 100);
        colorValues[1] = String.format(Locale.getDefault(), "%d", mValues[1] / 100);
        colorValues[2] = String.format(Locale.getDefault(), "%d", mValues[2] / 100);
        return colorValues;
    }

    public String[] decodeVOC(Integer... mValues) {
        //TODO: decode using byte values
        String[] vocValues = new String[2];
        vocValues[0] = String.format(Locale.getDefault(), "%d", mValues[0]);
        vocValues[1] = String.format(Locale.getDefault(), "%d", mValues[1]);
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

    public String decodeHeartRate(Integer... mValues) {
        //TODO: decode using byte values
        String heartRateValue = "72";
        return heartRateValue;
    }

    public void decodeSound(Integer... mValues) {
        //TODO: decode using byte values
    }

    public String[] decodeTemperature(Integer... mValues) {
        //TODO: decode using byte values
        String[] temperatureValues = new String[2];
        temperatureValues[0] = (mValues[0] / 100) + "";
        temperatureValues[1] = (mValues[1] / 100 - 32) * 5 / 9 + "";
        return temperatureValues;
    }

    public String[] decodeTemperature2(Integer... mValues) {
        //TODO: decode using byte values
        String[] temperatureValues = new String[2];
        temperatureValues[0] = "31";
        temperatureValues[1] = "32";

        return temperatureValues;

    }
}
