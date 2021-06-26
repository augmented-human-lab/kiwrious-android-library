package org.ahlab.kiwrious.android.service;

import android.util.Log;

import org.ahlab.kiwrious.android.Plugin;
import org.ahlab.kiwrious.android.models.ServiceBlockingQueue;
import org.ahlab.kiwrious.android.serial.QueueExtractor;
import org.ahlab.kiwrious.android.tasks.SensorDecoder;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SENSOR_TYPE;
import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SERIAL_FRAME_SIZE_RX;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_CONDUCTIVITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HEART_RATE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HUMIDITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_SOUND;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_TEMPERATURE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_UV;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_VOC;

public class QueueReader extends Thread {

    private final String TAG = QueueReader.class.getName();

    private final BlockingQueue blockingQueueRx;
    private final BlockingQueue serviceQueue;
    private boolean isRunning;

    private final SensorDecoder sensorDecoder;

    private final Plugin plugin;

    public QueueReader (Plugin plugin) {
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
            int[] eData = new int[KIWRIOUS_SERIAL_FRAME_SIZE_RX];
            int[] aux;
            try {
                aux = ServiceBlockingQueue.getServiceStatus() ? (int[]) serviceQueue.take() : (int[]) blockingQueueRx.poll(250, TimeUnit.MILLISECONDS);
                System.arraycopy(aux, 0, eData, 0, eData.length);
                Object[] boxedStream = Arrays.stream(eData).boxed().toArray();
                Integer[] publishArray = new Integer[KIWRIOUS_SERIAL_FRAME_SIZE_RX];
                for(int i = 0 ; i<KIWRIOUS_SERIAL_FRAME_SIZE_RX; i++){
                    publishArray[i] = (int)boxedStream[i];
                }
                decode(publishArray);


            } catch (Exception e) {
                Log.e(TAG, "Serial Read Thread Interrupted");
            }
        }
        super.run();
    }

    private void decode(Integer... values) {
        switch (values[KIWRIOUS_SENSOR_TYPE]) {
            case SENSOR_CONDUCTIVITY:
                sensorDecoder.decodeConductivity(values);
                break;
            case SENSOR_HEART_RATE:
                sensorDecoder.decodeHeartRate(values);
                break;
            case SENSOR_HUMIDITY:
                String[] humidityValues = sensorDecoder.decodeHumidity(values).split("\\s+");
                plugin.setTemperature(Float.parseFloat(humidityValues[0]));
                plugin.setHumidity(Float.parseFloat(humidityValues[1]));
                break;
            case SENSOR_SOUND:
                sensorDecoder.decodeSound(values);
                break;
            case SENSOR_TEMPERATURE:
                sensorDecoder.decodeTemperature(values);
                break;
            case SENSOR_UV:
                sensorDecoder.decodeUV(values);
                break;
            case SENSOR_VOC:
                sensorDecoder.decodeDefaultValues(values);
                break;
            default:
                break;
        }
    }
}
