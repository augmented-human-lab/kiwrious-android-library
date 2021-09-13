package org.ahlab.kiwrious.android.tasks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class SensorDecoder {

    public String[] decodeColor(byte[] sensorData) {
        //TODO: decode using byte values
        String[] colorValues = new String[3];

//        double r = Math.sqrt(ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());
//        double g = Math.sqrt(ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());
//        double b = Math.sqrt(ByteBuffer.wrap(sensorData, 10, 2).order(ByteOrder.LITTLE_ENDIAN).getShort());
//
//        colorValues[0] = String.format(Locale.getDefault(), "%d", r);
//        colorValues[1] = String.format(Locale.getDefault(), "%d", g);
//        colorValues[2] = String.format(Locale.getDefault(), "%d", b);

        return colorValues;
    }

    public String[] decodeVOC(byte[] sensorData) {

        String[] vocValues = new String[2];

        int voc = (sensorData[6] & 0xff | (sensorData[7] << 8));
        int co2 = (sensorData[8] & 0xff | (sensorData[7] << 8));

        vocValues[0] = Integer.toString(voc);
        vocValues[1] = Integer.toString(co2);;

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

        float a_temperature = ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
        float i_temperature = ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;

        temperatureValues[0] = Float.toString(a_temperature); // (mValues[0] / 100) + "";
        temperatureValues[1] = Float.toString(i_temperature); //(mValues[1] / 100 - 32) * 5 / 9 + "";

        return temperatureValues;
    }

    public String[] decodeTemperature2(byte[] sensorData) {
        String[] temperatureValues = new String[2];

        float a_temperature = ByteBuffer.wrap(sensorData, 6, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() /100;// BitConverter.ToInt16(data.Skip(6).Take(2).ToArray(), 0) / 100;
        short x = ByteBuffer.wrap(sensorData, 8, 2).order(ByteOrder.LITTLE_ENDIAN).getShort(); // BitConverter.ToUInt16(data.Skip(8).Take(2).ToArray(), 0);
        float a = ByteBuffer.wrap(sensorData, 10, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat(); //BitConverter.ToSingle(data.Skip(10).Take(4).ToArray(), 0);
        float b = ByteBuffer.wrap(sensorData, 14, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat(); //BitConverter.ToSingle(data.Skip(14).Take(4).ToArray(), 0);
        float c = ByteBuffer.wrap(sensorData, 18, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat(); //BitConverter.ToSingle(data.Skip(18).Take(4).ToArray(), 0);
        float d_temperature = (float)(Math.round(((a * Math.pow(x, 2)) / Math.pow(10, 5) + b * x + c)));

        temperatureValues[0] = String.format(Locale.getDefault(), "%.0f", a_temperature); //Float.toString(a_temperature); // (mValues[0] / 100) + "";
        temperatureValues[1] = String.format(Locale.getDefault(), "%.0f", d_temperature); //Float.toString(d_temperature); //(mValues[1] / 100 - 32) * 5 / 9 + "";

        return temperatureValues;
    }
}
