package org.ahlab.kiwrious.android.serial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import org.ahlab.kiwrious.android.utils.Constants;


import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SENSOR_TYPE;
import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SERIAL_FRAME_SIZE_RX;
import static org.ahlab.kiwrious.android.utils.Constants.KIWRIOUS_SERIAL_FRAME_SIZE_TX;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_COLOUR;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_CONDUCTIVITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HEART_RATE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_HUMIDITY;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_SOUND;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_TEMPERATURE;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_UV;
import static org.ahlab.kiwrious.android.utils.Constants.SENSOR_VOC;

// - - - - - - - - - - - - - - - - - - - - - - -
// - - -             Info Class            - - -
// - - - - - - - - - - - - - - - - - - - - - - -
//This class receives a USBManager Service from the main activity (base context)
//Creates a FTDI Driver
//Creates a hash table with a serial reception queue and a serial transmission queue
//Starts two threads:
//  -serialInterface:   Handles serial transmission/reception
//  -serialProcessing:  Handles the serial data acquired
public class SerialCommunication extends Activity implements SerialInterface {

    private static SerialCommunication instance;
    private static QueueExtractor queueExtractor;

    private static BlockingQueue blockingQueueTx;
    private static BlockingQueue blockingQueueRx;

    public static Hashtable<String, Queue<int[]>> serialQueueRx = null;
    public static Hashtable<String, Queue<byte[]>> serialQueueTx = null;

    public static Queue<int[]> queueRx = null;
    public static Queue<byte[]> queueTx = null;

    private static Object mSerialSemaphore = new Object();

    final static public String OM_MAC_ADDRESS = "00:21:13:00:2C:26";


    public long mTimer = System.currentTimeMillis();

    serialInterface     mSerialInterface;
    serialProcessing    mSerialProcessing;
    SerialUI            mSerialUI;


    FTDriver            mFTDIDriver;
    Context             mContext;

    private IntentFilter mUSBFilter;
    private UsbManager mUSBManager;
    private detachUSBReceiver mDetachUSBReceiver;
    private attachUSBReceiver mAttachUSBReceiver;

    final static public int SENSOR_TYPE_UV_LIGHT		= 		1;	// UV and light
    final static public int SENSOR_TYPE_BODY_TEMP		= 		2;	// Body temperature and ambient temperature
    final static public int SENSOR_TYPE_COLOUR			= 		3;	// R G B
    final static public int SENSOR_TYPE_CONDUCTIVITY    = 		4;	// Resistance
    final static public int SENSOR_TYPE_HEART_RATE		= 		5;	// Heart rate
    final static public int SENSOR_TYPE_VOC				= 		6;	// VOC
    final static public int SENSOR_TYPE_HUMIDITY		= 		7;
    final static public int SENSOR_TYPE_SOUND			= 		8;


    private SerialCommunication (Context context) {

        this.mContext = context;


        // - - - - - - - - - - - - - - - - - - - - - - -
        // - - -         USB Service Request       - - -
        // - - - - - - - - - - - - - - - - - - - - - - -

        Log.w("serialCommunication", "                                                                   ");
        Log.w("serialCommunication", "- - - - - - - - - - - - CREATING INSTANCE - - - - - - - - - - - - -");
        Log.w("serialCommunication", "---------------------- Loading USB Service ------------------------");

        if (mDetachUSBReceiver == null) {
            mDetachUSBReceiver = new detachUSBReceiver();
        }
        if (mAttachUSBReceiver == null) {
            mAttachUSBReceiver = new attachUSBReceiver();
        }

        if (mUSBFilter == null) {
            mUSBFilter = new IntentFilter();

            if (mDetachUSBReceiver != null && mUSBFilter != null) {
                mUSBFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                this.mContext.registerReceiver(mDetachUSBReceiver, mUSBFilter);
            }
            if (mAttachUSBReceiver != null && mUSBFilter != null) {
                mUSBFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                this.mContext.registerReceiver(mAttachUSBReceiver, mUSBFilter);
            }

            mUSBManager = (UsbManager) this.mContext.getSystemService(this.mContext.USB_SERVICE);

            initSerialManager (this.mContext);
        }


        Log.w("serialCommunication", "- - - - - - - - - - - CREATING INSTANCE END - - - - - - - - - - - -");
        Log.w("serialCommunication", "                                                                   ");

    }

    public static SerialCommunication getInstance(Context eContext){
        if (instance == null){
            instance = new  SerialCommunication(eContext);
        }
        return instance;
    }

    public boolean startCommunications () {

        if (mFTDIDriver == null) return false;
        serialQueueRx = new Hashtable<String, Queue<int[]>>();
        serialQueueTx = new Hashtable<String, Queue<byte[]>>();
        queueRx = new LinkedList<int []>();
        queueTx = new LinkedList<byte []>();

        queueExtractor = QueueExtractor.getInstance();
        blockingQueueRx = queueExtractor.getQueueRx();
        blockingQueueTx = queueExtractor.getQueueRx();


        synchronized (mSerialSemaphore) {
            serialQueueRx.put(OM_MAC_ADDRESS, queueRx);
        }
        synchronized (mSerialSemaphore) {
            serialQueueTx.put(OM_MAC_ADDRESS, queueTx);
        }

        Log.w("serialCommunication", "                                                                   ");
        Log.w("serialCommunication", "- - - - - - - - - -  START COMMUNICATIONS - - - - - - - - - - - - -");
        Log.w("serialCommunication", "------------------ Serial Queues Initialized ----------------------");
        mSerialInterface = new serialInterface();
        new Thread(mSerialInterface).start();
        Log.w("serialCommunication", "---------------- Serial Interface Initialized ---------------------");


        mSerialProcessing = new serialProcessing ();
        new Thread(mSerialProcessing).start();

        //mSerialUI = new SerialUI();
        //new Thread(mSerialUI).start();
        Log.w("serialCommunication", "---------------- Serial Interface UI Initialized ---------------------");
        Log.w("serialCommunication", "- - - - - - - - - - -  END COMMUNICATIONS - - - - - - - - - - - - -");
        Log.w("serialCommunication", "                                                                   ");

        return true;
    }


    class serialInterface extends ThreadedTask {
        @Override
        public void run() {

            byte [] mSerialBuffer = new byte[1024];
            int [] mSerialFrameOmRx = new int [KIWRIOUS_SERIAL_FRAME_SIZE_RX];
            byte [] mSerialFrameOmTx = new byte [KIWRIOUS_SERIAL_FRAME_SIZE_TX];

            int [] _dataSensor = new int [20];
            int mBytesAvailable;
            int mParser = 0;
            int mState  = 0;


            int _stateSerial 	= 0;
            int _auxStateSerial	= 1;

            int _packetType 	= 0;
            int _packetTypeH	= 0;
            int _packetTypeL	= 0;


            int _data		    = 0;
            int _dataL		    = 0;
            int _dataH	        = 0;

            int _dataLength		= 0;
            int _dataLengthH	= 0;
            int _dataLengthL	= 0;
            int _dataLengthAux	= 0;

            int _sNumber		= 0;
            int _sNumberH		= 0;
            int _sNumberL		= 0;


            Log.w("serialCommunication", "-------------------------------------------------------------------");
            Log.w("serialCommunication", "--------------- Serial Interface Thread Starting ------------------");
            Log.w("serialCommunication", "-------------------------------------------------------------------");
            while (taskRunning) {

                // - - - - - - - - - - - - - - - - - - - - - - -
                // - - -        Serial Transmission        - - -
                // - - - - - - - - - - - - - - - - - - - - - - -

//                if (blockingQueueTxRead (mSerialFrameOmTx)) {                                         // Read Frame from the Serial Transmission Queue
//
//                    if (mFTDIDriver != null) {
//                        // Log.w("serialCommunication","Serial Interface Thread: mSerialFrameOmTx: " + mSerialFrameOmTx [0] + " ][ " + mSerialFrameOmTx [1] + " ][ " + mSerialFrameOmTx [2] + " ][ " + mSerialFrameOmTx [3]);
//                        Log.w ("pattern","[" + mSerialFrameOmTx[0] +"]["+ mSerialFrameOmTx[1] +"]["+ mSerialFrameOmTx[2] +"]["
//                                + mSerialFrameOmTx[3] +"]["+ mSerialFrameOmTx[4] +"]["+ mSerialFrameOmTx[5] +"]["+ mSerialFrameOmTx[6] +"]["
//                                + mSerialFrameOmTx[7] +"]["+ mSerialFrameOmTx[8] +"]["+ mSerialFrameOmTx[9] +"]["+ mSerialFrameOmTx[10] +"]");
//
//                        mFTDIDriver.write(mSerialFrameOmTx,OM_SERIAL_FRAME_SIZE_TX,1);                                        // Send the frame over the serial Interface
//                    }
//                }

                // - - - - - - - - - - - - - - - - - - - - - - -
                // - - -           Serial Reception        - - -
                // - - - - - - - - - - - - - - - - - - - - - - -

                // Read Frame from the Serial Transmission Queue
                if (mFTDIDriver!= null) {
                    mBytesAvailable = mFTDIDriver.read(mSerialBuffer,1);
//                    blockingQueueRxWrite(mSerialFrameOmRx);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//
//                    }

                    for (int i = 0; i < mBytesAvailable; i++) {

                        switch (_stateSerial) {
                            // - - - - - Header - - - - - -
                            case 0://Read Header A
                                if (mSerialBuffer[i] == 0x0A) {
                                    _stateSerial = 1;
                                }
                                break;
                            case 1://Read Header B
                                if (mSerialBuffer[i] == 0x0A) {
                                    _stateSerial = 2;
                                    for (int k = 0;k < KIWRIOUS_SERIAL_FRAME_SIZE_RX;k++) {
                                        mSerialFrameOmRx[k] = 0;
                                    }
                                } else {
                                    _stateSerial = 0;
                                }
                                break;
                            // - - - - - Packet Type - - - - - -
                            case 2://Read Packet Type L
                                _packetTypeL = (0xFF & mSerialBuffer[i]);
                                _stateSerial = 3;
                                break;
                            case 3://Read Packet Type H
                                _packetTypeH = (0xFF & mSerialBuffer[i]);
                                _packetType = (_packetTypeH << 8) | _packetTypeL;
                                _stateSerial = 4;
                                break;
                            // - - - - - Data Length - - - - - -
                            case 4://Read Data Length L
                                _dataLengthL = (0xFF & mSerialBuffer[i]);
                                _stateSerial = 5;
                                break;
                            case 5://Read Data Length H
                                _dataLengthH = (0xFF & mSerialBuffer[i]);
                                _dataLength = (_dataLengthH << 8) | _dataLengthL;
                                _dataLengthAux 	= _dataLength;
                                _stateSerial = 6;
                                break;
                            //- - - - - - Data - - - - - -
                            case 6://Read Data L
                                _dataL = (0xFF & mSerialBuffer[i]);
                                _stateSerial = 7;
                                break;
                            case 7://Read Data H
                                _dataH = (0xFF & mSerialBuffer[i]);
                                _data = (_dataH << 8) | _dataL;
                                _dataSensor [_dataLength - _dataLengthAux] = _data;
                                if (--_dataLengthAux > 0) {
                                    _stateSerial = 6;
                                } else {
                                    _stateSerial = 8;
                                }
                                break;
                            // - - - - - Sequence Number - - - - - -
                            case 8://Read Sequence Number L
                                _sNumberL = (0xFF & mSerialBuffer[i]);
                                _stateSerial = 9;
                                break;
                            case 9://Read Sequence Number H
                                _sNumberH = (0xFF & mSerialBuffer[i]);
                                _sNumber = (_sNumberH << 8) | _sNumberL;
                                _stateSerial = 10;
                                break;
                            // - - - - - Footer - - - - - -
                            case 10://Read Footer A
                                if (mSerialBuffer[i] == 0x0B) {
                                    _stateSerial = 11;
                                }
                                break;
                            case 11://Read Footer B
                                if (mSerialBuffer[i] == 0x0B) {
                                    if (_packetTypeL == SENSOR_TYPE_COLOUR) {
                                        mSerialFrameOmRx [0] = _dataSensor[0];
                                        mSerialFrameOmRx [1] = _dataSensor[1];
                                        mSerialFrameOmRx [2] = _dataSensor[2];
                                        mSerialFrameOmRx [3] = _dataSensor[3];
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_COLOUR;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_CONDUCTIVITY) {
                                        mSerialFrameOmRx [0] = _dataSensor[0];
                                        mSerialFrameOmRx [1] = _dataSensor[1];
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_CONDUCTIVITY;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_HEART_RATE) {
                                        Log.w("serialCommunication", "SENSOR_TYPE_HEART_RATE");
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_HEART_RATE;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_HUMIDITY) {
                                        mSerialFrameOmRx [0] = _dataSensor[0];
                                        mSerialFrameOmRx [1] = _dataSensor[1];
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_HUMIDITY;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_SOUND) {
                                        Log.w("serialCommunication", "SENSOR_TYPE_SOUND");
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_SOUND;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_BODY_TEMP) {
                                        Log.w("serialCommunication", "SENSOR_TYPE_BODY_TEMP");
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_TEMPERATURE;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_UV_LIGHT) {
                                        mSerialFrameOmRx [0] = _dataSensor[0];
                                        mSerialFrameOmRx [1] = _dataSensor[1];
                                        mSerialFrameOmRx [2] = _dataSensor[2];
                                        mSerialFrameOmRx [3] = _dataSensor[3];
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_UV;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                    if (_packetTypeL == SENSOR_TYPE_VOC) {
                                        mSerialFrameOmRx [0] = _dataSensor[0];
                                        mSerialFrameOmRx [1] = _dataSensor[1];
                                        mSerialFrameOmRx [KIWRIOUS_SENSOR_TYPE] = SENSOR_VOC;
                                        blockingQueueRxWrite(mSerialFrameOmRx);
                                    }
                                }
                                _stateSerial = 0;
                                break;
                            default:
                                _stateSerial = 0;
                                break;
                        }
                    }
                }
            }
            Log.w("serialCommunication", "-------------------------------------------------------------------");
            Log.w("serialCommunication", "----------------- Serial Interface Thread Stopped -----------------");
            Log.w("serialCommunication", "-------------------------------------------------------------------");
        }
    }

    class SerialUI extends ThreadedTask {
        @Override
        public void run() {
            while (taskRunning) {
                int[] myData = new int[KIWRIOUS_SERIAL_FRAME_SIZE_RX];
                blockingQueueRxRead(myData);

            }
        }
    }



    class serialProcessing extends ThreadedTask {
        @Override
        public void run() {


            Log.w("serialCommunication", "-------------- SERIAL PROCESSING THREAD STARTING ------------------");


            int[] mSerialFrameOmRx = new int [KIWRIOUS_SERIAL_FRAME_SIZE_RX];
            while (taskRunning) {


                //  if (blockingQueueRxRead (mSerialFrameOmRx)) {                                         // Get information previously acquired from the serial interface
                //       Log.w("serialCommunication","Serial Processing Thread: mSerialFrameOmRx: [" + mSerialFrameOmRx [0] + "] [" + mSerialFrameOmRx [1] + "] [" + mSerialFrameOmRx [2] + "] [" + mSerialFrameOmRx [3]+ "] ");
                //   }
            }

            Log.w("serialCommunication", "--------------- SERIAL PROCESSING THREAD STOPPED ------------------");
            Log.w("serialCommunication", "                                                                   ");
        }

    }
    /*Method to add an element on a given serial reception queue*/
    static private void blockingQueueRxWrite(int eData[]) {
        if (blockingQueueRx != null) {                                                          //Queues Hash table contains this Mac Address
            int[] mData = Arrays.copyOf(eData, eData.length);
            boolean isCapacityAvailable = blockingQueueRx.offer(mData);                                                       //Writing the information in the queue
            if (!isCapacityAvailable) blockingQueueRx.clear();
        }
    }
    /*Method to add an element on a given serial transmission queue*/
    static private void blockingQueueTxWrite(byte eData[]) {
        if (blockingQueueTx != null) {                             //Queues Hash table contains this Mac Address.
            byte [] mData = Arrays.copyOf(eData, eData.length);
            blockingQueueTx.offer(mData);                                                       //Writing the information in the queue
        }
    }
    /*Method to extract an element of a given serial reception queue*/
    static private boolean blockingQueueRxRead (int eData[]) {
        int[] aux;
        try {
            aux = (int[]) blockingQueueRx.take();
            System.arraycopy(aux, 0, eData, 0, eData.length);            //Copying element

            Log.w("serialCommunicationUI", "-------------------------------------------------------------------");
            Log.w("serialCommunicationUI",String.valueOf(aux));
            Log.w("serialCommunicationUI", "-------------------------------------------------------------------");
            return true;
        } catch (InterruptedException e) {
            Log.e("Interrupt", "Read Thread Interrupted");
            Thread.currentThread().interrupt();
            return false;
        }
    }
    /*Method to extract an element of a given serial reception queue*/
    static private boolean blockingQueueTxRead (byte eData[]) {
        if (blockingQueueTx != null) {
            byte[] aux;
            aux = (byte[]) blockingQueueTx.poll();
            System.arraycopy(aux, 0, eData, 0, eData.length);            //Copying element
            return true;
        }
        return false;
    }


    public void stopSerialCommunications () {
        Log.w("serialCommunication", "                                                                   ");
        Log.w("serialCommunication", "- - - - - - - - - STOPPING SERIAL COMMUNICATIONS  - - - - - - - - -");
        if (mSerialInterface != null) {
            mSerialInterface.stop();
            Log.w("serialCommunication", "---------------------- Serial Interface Stopped -------------------");
        }
        if (mSerialProcessing != null) {
            mSerialProcessing.stop();
            Log.w("serialCommunication", "--------------------- Serial Processing Stopped -------------------");
        }

        if (mSerialUI != null) {
            mSerialUI.stop();
            Log.w("serialCommunication", "--------------------- Serial UI Stopped -------------------");
        }

        QueueExtractor.disableQueue();

        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_FTDI_FAIL);
        mContext.sendBroadcast(intent);

        synchronized (mSerialSemaphore) {                                                                //To prevent fatal exceptions
            if (serialQueueRx != null) {
                Log.w("serialCommunication", "----------------------- serialQueueRx Disabled --------------------");
                serialQueueRx = null;
            }
            if (serialQueueTx != null) {
                Log.w("serialCommunication", "----------------------- serialQueueTx Disabled --------------------");
                serialQueueTx = null;
            }
            if (queueRx != null) {
                Log.w("serialCommunication", "------------------------- queueRx Disabled ------------------------");
                queueRx = null;
            }
            if (queueTx != null) {
                Log.w("serialCommunication", "------------------------- queueTx Disabled ------------------------");
                queueTx = null;
            }
        }

        if (mFTDIDriver != null) {
            mFTDIDriver.end();
            mFTDIDriver = null;
            Log.w("serialCommunication", "----------------------- USB Manager cleaned -----------------------");
        }
        Log.w("serialCommunication", "- - - - - - - - STOPPING SERIAL COMMUNICATION END - - - - - - - - -");
        Log.w("serialCommunication", "                                                                   ");

    }

    public String getDeviceProductName () {
        try {
            return mFTDIDriver.getDeviceProductName();
        } catch (Exception mException) {
            return mException.getMessage();
        }
    }


    public class attachUSBReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {

                Log.w("serialCommunication", "                                                                   ");
                Log.w("serialCommunication", "- - - - - - - - - - -  USB ACTION ATTACHED  - - - - - - - - - - - -");
                initSerialManager (context);
                Log.w("serialCommunication", "- - - - - - - - - - USB ACTION ATTACHED END - - - - - - - - - - - -");
                Log.w("serialCommunication", "                                                                   ");

            }
        }
    }

    public class detachUSBReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                Log.w("serialCommunication", "                                                                   ");
                Log.w("serialCommunication", "- - - - - - - - - - -  USB ACTION DETACHED  - - - - - - - - - - - -");
                freeUSBResources();
                Log.w("serialCommunication", "- - - - - - - - - - USB ACTION DETACHED END - - - - - - - - - - - -");
                Log.w("serialCommunication", "                                                                   ");
            }
        }
    }

    public void initSerialManager (Context context) {

        stopSerialCommunications ();



        if (mUSBManager != null) {
            // - - - - - - - - - - - - - - - - - - - - - - -
            // - - -       Serial Service Request      - - -
            // - - - - - - - - - - - - - - - - - - - - - - -
            Log.w("serialCommunication", "---------------------- Load Serial Service ------------------------");
            mFTDIDriver = new FTDriver(mUSBManager);
            Log.w("serialCommunication", "-----------------  USB - FTDI Manager Loaded  ---------------------");
            if (!mFTDIDriver.isConnected()) {
                if (mFTDIDriver.begin(FTDriver.BAUD230400)) {

                    QueueExtractor.enableQueue();

                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_FTDI_SUCCESS);
                    context.sendBroadcast(intent);

                    Toast.makeText(context, "Dongle Setup Success", Toast.LENGTH_SHORT).show();
                    Log.w("serialCommunication", "------------------ FTDI Initialization Success --------------------");

                    startCommunications();
                } else {

                    Toast.makeText(context, "Dongle Setup Fail", Toast.LENGTH_SHORT).show();
                    Log.w("serialCommunication", "------------------ FTDI Initialization Failed ---------------------");
                }
            } else {
                Log.w("serialCommunication", "------------------ FTDI Already Initialized -----------------------");
            }
        }
    }


    private void freeUSBResources() {
//        if (mDetachUSBReceiver != null) {
//            this.unregisterReceiver(mDetachUSBReceiver);                                               //Unregistering brodadcast receiver.
//            mDetachUSBReceiver = null;
//        }
//        if (mAttachUSBReceiver != null) {
//            this.unregisterReceiver(mAttachUSBReceiver);                                               //Unregistering brodadcast receiver.
//            mAttachUSBReceiver = null;
//        }
//        if (mUSBFilter != null) {
//            mUSBFilter = null;
//        }

        this.stopSerialCommunications();
    }



    public void testSend (){

        byte[] mData = new byte[KIWRIOUS_SERIAL_FRAME_SIZE_TX];


        mData[0] = '#';                                                         // header
        mData[1] = 0;                                                           // command
        mData[2] = (byte) 1;                               // duration
        mData[3] = 2;                                            // Power A
        mData[4] = 3;                                            // Power B
        mData[5] = 3;                                            // Power C
        mData[6] = 3;                                            // Power D
        mData[7] = 3;                                            // Power E
        mData[8] = 3;                                            // Power F
        mData[9] = 3;                                           // eData A
        mData[10] = 3;                                          // eData B
        mData[11] = 3;                                          // eData C
        mData[12] = 3;                                          // eData D
        mData[13] = 3;                                          // eData E
        mData[14] = 3;                                          // eData F
        for (int i = 15; i < KIWRIOUS_SERIAL_FRAME_SIZE_TX; i++) {
            mData[i] = 0;
        }
        blockingQueueTxWrite(mData);
        Log.w("serialCommunication", "Added to TX queue");
    }

    @Override
    public void addFrameSerialQueueTX (byte eData[]){
        blockingQueueTxWrite(eData);
        Log.w("serialCommunication", "Added to TX queue");
    }

    @Override
    public void getFrameSerialQueueRX(int eData[]){
        blockingQueueRxRead (eData);
        Log.w("serialCommunication", "Got element from RX queue");
    }
}
