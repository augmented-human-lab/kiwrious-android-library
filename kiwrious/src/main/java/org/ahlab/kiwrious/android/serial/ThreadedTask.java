package org.ahlab.kiwrious.android.serial;

public abstract class ThreadedTask implements Runnable {
    protected volatile boolean taskRunning = true;

    public boolean isRunning (){
        return taskRunning;
    }

    public void stop (){
        taskRunning = false;
    }

}
