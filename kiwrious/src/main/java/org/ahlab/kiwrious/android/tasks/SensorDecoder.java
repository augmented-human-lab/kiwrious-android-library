package org.ahlab.kiwrious.android.tasks;

import java.util.Locale;

public class SensorDecoder {

    /**
     * COLOUR:
     *   red      --> mValues[0]
     *   green    --> mValues[1]
     *   blue     --> mValues[2]
     *   white    --> mValues[3]
     *
     * VOC:
     *   temperatureVOC   --> mValues[0]
     *   equivalentCO2    --> mValues[1]
     */

    public void decodeDefaultValues(Integer... mValues) {

        for (Integer mValue : mValues) {
            String.format(Locale.getDefault(),"%d", mValue);
        }
    }

    public String decodeConductivity(Integer... mValues) {

        StringBuilder message = new StringBuilder();
        long resistance = (long) mValues[0] * mValues[1];

        String uSiemens = "0";
        if (resistance != 0f) {
            uSiemens = String.format(Locale.getDefault(), "%.2f",
                    (1 / (float) resistance) * 1000000);
        }

        return (message.append(resistance).append(" ").append(uSiemens)).toString() ;
    }

    public String decodeHumidity(Integer... mValues) {

        StringBuilder value = new StringBuilder();
        String message;
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
            message = "";

            if (data >= 10000) {
                hundreds = data / 10000;
                data -= hundreds * 10000;
                message += Integer.toString(hundreds);
            }

            if (data >= 1000) {
                tens = data / 1000;
                data -= tens * 1000;
                message += Integer.toString(tens);
            } else if (hundreds > 0) {
                message += tens;
            }

            if (data >= 100) {
                units = data / 100;
                data -= units * 100;
                message += Integer.toString(units);
            } else if (tens > 0) {
                message += units;
            }

            if (data >= 10) {
                decimals = data / 10;
                data -= decimals * 10;
            }
            message += "." + decimals;

            cents = data;
            message += Integer.toString(cents);
            value.append(" ").append(message);
        }
        return value.toString().trim();
    }

    public String decodeUV(Integer... mValues) {

        StringBuilder message = new StringBuilder();
        long H;
        long L;

        for (int i = 0; i < 2; i++) {

            H = mValues[i * 2 + 1];
            L = mValues[i * 2];
            message.append(" ").append(String.format(Locale.getDefault(), "%.2f", Float.intBitsToFloat((int) ((H << 16) | L))));
        }
        return message.toString().trim();
    }

    public void decodeHeartRate(Integer... mValues) {
        //TODO: Implement Heart Rate Processing
    }

    public void decodeSound(Integer... mValues) {
        //TODO: Implement Sound Processing
    }

    public void decodeTemperature(Integer... mValues) {
        //TODO: Implement Temperature Processing
    }
}
