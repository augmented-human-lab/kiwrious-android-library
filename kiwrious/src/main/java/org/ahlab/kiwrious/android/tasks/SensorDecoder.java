package org.ahlab.kiwrious.android.tasks;

import android.util.Log;

import java.util.Locale;

public class SensorDecoder {

    /**
     * Conductivity:
     * Resistance (Ohm) = mValues[0] * mValues[1]
     * <p>
     * Humidity
     * Temperature (C) = mValues[0] / 100
     * Humidity (%)	= mValues[1] / 100
     * <p>
     * VoC
     * tVOC (ppb)		= mValues[0]
     * CO2eq(ppm)		= mValues[1]
     * Colour
     * Red				= mValues[0]
     * Green			= mValues[1]
     * Blue			= mValues[2]
     * White			= mValues[3]
     * <p>
     * UV and Light
     * Lux				= (float) (mValues[0] + (mValues[1] << 8))
     * UV				= (float) (mValues[2] + (mValues[3] << 8))
     */

    public String[] decodeDefaultValues(Integer... mValues) {

        String[] defaultValues = new String[mValues.length];
        for (int i = 0; i < mValues.length; i++) {
            defaultValues[i] = String.format(Locale.getDefault(), "%d", mValues[i]);
        }
        return defaultValues;
    }

    public String[] decodeVOC(Integer... mValues){
        String[] vocValues = new String[2];
        vocValues[0] = String.format(Locale.getDefault(), "%d", mValues[0]);
        vocValues[1] = String.format(Locale.getDefault(), "%d", mValues[1]);
        Log.i("sankha voc ",vocValues[0] + " " + vocValues[1]);
        return  vocValues;
    }

    public String[] decodeConductivity(Integer... mValues) {

        String[] conductivityValues = new String[2];
        long resistance = (long) mValues[0] * mValues[1];

        String uSiemens = "0";
        if (resistance != 0) {
            uSiemens = String.format(Locale.getDefault(), "%.2f",
                    (1 / (float) resistance) * 1000000);
        }

        conductivityValues[0] = String.valueOf(resistance);
        conductivityValues[1] = uSiemens;

        return conductivityValues;
    }

    public String[] decodeHumidity(Integer... mValues) {

        String[] humidityValues = new String[2];
        StringBuilder message = new StringBuilder();
        int hundreds;
        int tens;
        int units;
        int decimals;
        int cents;
        int data;

        for (int i = 0; i < 2; i++) {
            hundreds = 0;
            tens = 0;
            units = 0;
            decimals = 0;
            data = mValues[i];

            if (data >= 10000) {
                hundreds = data / 10000;
                data -= hundreds * 10000;
                message.append(hundreds);
            }

            if (data >= 1000) {
                tens = data / 1000;
                data -= tens * 1000;
                message.append(tens);
            } else if (hundreds > 0) {
                message.append(tens);
            }

            if (data >= 100) {
                units = data / 100;
                data -= units * 100;
                message.append(units);
            } else if (tens > 0) {
                message.append(units);
            }

            if (data >= 10) {
                decimals = data / 10;
                data -= decimals * 10;
            }
            message.append(".").append(decimals);

            cents = data;
            message.append(cents);

            humidityValues[i] = message.toString();
            message.setLength(0);
        }
        return humidityValues;
    }

    public String[] decodeUV(Integer... mValues) {

        String[] lightValues = new String[2];
        long H;
        long L;

        for (int i = 0; i < 2; i++) {

            H = mValues[i * 2 + 1];
            L = mValues[i * 2];
            lightValues[i] = String.format(Locale.getDefault(), "%.2f",
                    Float.intBitsToFloat((int) ((H << 16) | L)));
        }
        return lightValues;
    }

    public String decodeHeartRate(Integer... mValues) {
        String heartRateValue = "72";
        // serial decode code here...
        return heartRateValue;
    }

    public void decodeSound(Integer... mValues) {
        //TODO: Implement Sound Processing
    }

    public String[] decodeTemperature(Integer... mValues) {

        String[] temperatureValues = new String[2];
        temperatureValues[0] = (mValues[0] / 100)+""; //"41";
        temperatureValues[1] = (mValues[1]/100 - 32)*5 / 9+"";

        // serial decode part here...

        return temperatureValues;

    }

    public String[] decodeTemperature2(Integer... mValues) {

        String[] temperatureValues = new String[2];
        temperatureValues[0] = "31";
        temperatureValues[1] = "32";

        // serial decode part here...

        return temperatureValues;

    }
}
