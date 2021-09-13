package org.ahlab.kiwrious.android.service;

import android.util.Log;

import org.ahlab.kiwrious.android.Plugin;
import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;
import org.ahlab.kiwrious.android.tasks.SensorDecoder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SENSOR_TYPE;
import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SERIAL_FRAME_SIZE_RX;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_COLOUR;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_CONDUCTIVITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HEART_RATE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HUMIDITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_SOUND;
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

    private final Plugin plugin;

    public QueueReader(Plugin plugin) {
        this.isRunning = true;
        this.plugin = plugin;

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
        switch (sensorData[KIWRIOUS_SENSOR_TYPE]) {
            //TODO: complete decode calls using bye array data
            case SENSOR_COLOUR:
                String[] colorValues = sensorDecoder.decodeColor(sensorData);
                plugin.setR(Integer.parseInt(colorValues[0]));
                plugin.setG(Integer.parseInt(colorValues[1]));
                plugin.setB(Integer.parseInt(colorValues[2]));
                break;
            case SENSOR_CONDUCTIVITY:
                String[] conductivityValues = sensorDecoder.decodeConductivity(sensorData);
                plugin.setResistance(Long.parseLong(conductivityValues[0]));
                plugin.setConductivity(Float.parseFloat(conductivityValues[1]));
                break;
            case SENSOR_HEART_RATE:
                String heartRateValue = sensorDecoder.decodeHeartRate(sensorData);
                plugin.setHeartRate(Integer.parseInt(heartRateValue));
                break;
            case SENSOR_HUMIDITY:
                Float[] humidityValues = sensorDecoder.decodeHumidity(sensorData);
                plugin.setTemperature(humidityValues[0]);
                plugin.setHumidity(humidityValues[1]);
                break;
            case SENSOR_SOUND:
//                sensorDecoder.decodeSound(values);
                break;
            case SENSOR_TEMPERATURE:
                String[] temperatureValues = sensorDecoder.decodeTemperature(sensorData);
                plugin.setAmbientTemperature(Integer.parseInt(temperatureValues[0]));
                plugin.setInfraredTemperature(Integer.parseInt(temperatureValues[1]));
                break;
            case SENSOR_TEMPERATURE2:
                String[] temperature2Values = sensorDecoder.decodeTemperature2(sensorData);
                plugin.setAmbientTemperature(Integer.parseInt(temperature2Values[0]));
                plugin.setInfraredTemperature(Integer.parseInt(temperature2Values[1]));
                break;
            case SENSOR_UV:
                String[] lightValues = sensorDecoder.decodeUV(sensorData);
                plugin.setLux(Long.parseLong(lightValues[0]));
                plugin.setUv(Float.parseFloat(lightValues[1]));
                break;
            case SENSOR_VOC:
                String[] vocValues = sensorDecoder.decodeVOC(sensorData);
                plugin.setVoc(Integer.parseInt(vocValues[0]));
                plugin.setCo2(Integer.parseInt(vocValues[1]));
                break;
            default:
                break;
        }
    }
}
