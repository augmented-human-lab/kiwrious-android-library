package org.ahlab.kiwrious.android.service;

import android.util.Log;

import org.ahlab.kiwrious.android.KiwriousReader;
import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.usb_serial.QueueExtractor;
import org.ahlab.kiwrious.android.tasks.SensorDecoder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SENSOR_TYPE_INDEX;
import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SERIAL_FRAME_SIZE_RX;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_COLOUR;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_CONDUCTIVITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HEART_RATE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HUMIDITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_TEMPERATURE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_TEMPERATURE2;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_UV;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_VOC;

public class QueueReader extends Thread {

    private final String TAG = QueueReader.class.getName();

    private final BlockingQueue blockingQueueRx;
    private final BlockingQueue serviceQueue;
    private boolean isRunning;

    private final SensorDecoder sensorDecoder;

    private final KiwriousReader kiwriousReader;

    public QueueReader(KiwriousReader kiwriousReader) {
        this.isRunning = true;
        this.kiwriousReader = kiwriousReader;

        ServiceBlockingQueue serviceBlockingQueue = ServiceBlockingQueue.getInstance();
        this.serviceQueue = serviceBlockingQueue.getServiceQueue();

        QueueExtractor queueExtractor = QueueExtractor.getInstance();
        blockingQueueRx = queueExtractor.getQueueRx();

        sensorDecoder = new SensorDecoder();
    }

    @Override
    public void run() {
        while (isRunning && QueueExtractor.getQueueStatus()) {
            byte[] publishArray = new byte[KIWRIOUS_SERIAL_FRAME_SIZE_RX];
            byte[] aux;
            try {
                aux = ServiceBlockingQueue.getServiceStatus() ? (byte[]) serviceQueue.take() : (byte[]) blockingQueueRx.poll(250, TimeUnit.MILLISECONDS);
                System.arraycopy(aux, 0, publishArray, 0, publishArray.length);
                decode(publishArray);

            } catch (Exception e) {
                Log.e(TAG, "Serial Read Thread Interrupted");
            }
        }
        super.run();
    }

    private void decode(byte[] sensorData) {
        kiwriousReader.setRawValues(sensorData);
        switch (sensorData[KIWRIOUS_SENSOR_TYPE_INDEX]) {
            case SENSOR_COLOUR:
                String[] colorValues = sensorDecoder.decodeColor(sensorData);
                kiwriousReader.setR(Integer.parseInt(colorValues[0]));
                kiwriousReader.setG(Integer.parseInt(colorValues[1]));
                kiwriousReader.setB(Integer.parseInt(colorValues[2]));
                break;
            case SENSOR_CONDUCTIVITY:
                String[] conductivityValues = sensorDecoder.decodeConductivity(sensorData);
                kiwriousReader.setResistance(Long.parseLong(conductivityValues[0]));
                kiwriousReader.setConductivity(Float.parseFloat(conductivityValues[1]));
                break;
            case SENSOR_HEART_RATE:
                String heartRateValue = sensorDecoder.decodeHeartRate(sensorData);
                kiwriousReader.setHeartRate(Integer.parseInt(heartRateValue));
                break;
            case SENSOR_HUMIDITY:
                Float[] humidityValues = sensorDecoder.decodeHumidity(sensorData);
                kiwriousReader.setTemperature(humidityValues[0]);
                kiwriousReader.setHumidity(humidityValues[1]);
                break;
            case SENSOR_TEMPERATURE:
                String[] temperatureValues = sensorDecoder.decodeTemperature(sensorData);
                kiwriousReader.setAmbientTemperature(Integer.parseInt(temperatureValues[0]));
                kiwriousReader.setInfraredTemperature(Integer.parseInt(temperatureValues[1]));
                break;
            case SENSOR_TEMPERATURE2:
                String[] temperature2Values = sensorDecoder.decodeTemperature2(sensorData);
                kiwriousReader.setAmbientTemperature(Integer.parseInt(temperature2Values[0]));
                kiwriousReader.setInfraredTemperature(Integer.parseInt(temperature2Values[1]));
                break;
            case SENSOR_UV:
                String[] lightValues = sensorDecoder.decodeUV(sensorData);
                kiwriousReader.setLux(Long.parseLong(lightValues[0]));
                kiwriousReader.setUv(Float.parseFloat(lightValues[1]));
                break;
            case SENSOR_VOC:
                String[] vocValues = sensorDecoder.decodeVOC(sensorData);
                kiwriousReader.setVoc(Integer.parseInt(vocValues[0]));
                kiwriousReader.setCo2(Integer.parseInt(vocValues[1]));
                break;
            default:
                Log.e("kiwrious-plugin", "unexpected sensor type "+sensorData[KIWRIOUS_SENSOR_TYPE_INDEX]);
                break;
        }
    }
}
