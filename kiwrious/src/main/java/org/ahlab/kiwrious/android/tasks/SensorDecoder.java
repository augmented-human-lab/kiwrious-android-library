package org.ahlab.kiwrious.android.tasks;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class SensorDecoder {

    private final ArrayList<TextView> textViews;

    public SensorDecoder(ArrayList<TextView> textViews) {
        this.textViews = textViews;
    }

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

        int i = 0;
        for (TextView textView : textViews) {
            textView.setText(String.format(Locale.getDefault(),"%d", mValues[i]));
            i++;
        }
    }

    public void decodeConductivity(Integer... mValues) {

        long resistance = (long) mValues[0] * mValues[1];
        float uSiemens = (1 / (float) resistance) * 1000000;
        textViews.get(0).setText(String.format(Locale.getDefault(), "%.2f", uSiemens));
    }

    public void decodeHumidity(Integer... mValues) {

        String message;
        int hundreds;
        int tens;
        int units;
        int decimals;
        int cents;
        int data;

        int i = 0;
        for (TextView textView : textViews) {
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

            textView.setText(message);
            i++;
        }
    }

    public void decodeUV(Integer... mValues) {

        String message;
        long H;
        long L;

        int i = 0;
        for (TextView textView : textViews) {

            H = mValues[i * 2 + 1];
            L = mValues[i * 2];
            message = String.format(Locale.getDefault(), "%.2f", Float.intBitsToFloat((int) ((H << 16) | L )));
            textView.setText(message);

            i++;
        }
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
