package org.ahlab.kiwrious.android.serial;

public interface SerialInterface {

    //Interface to be used for communicating with SerialCommunication
    void addFrameSerialQueueTX (byte eData[]);
    void getFrameSerialQueueRX(int eData[]);
}