package org.ahlab.kiwrious.android.serial;

public abstract class ThreadedTaskRunner {
    private ThreadedTask currentThreadedTask;

    protected abstract ThreadedTask newThreadedTask ();

    private void setCurrentThreadedTask (ThreadedTask eTask){
        if (currentThreadedTask != null && currentThreadedTask.isRunning()) stop();
        currentThreadedTask = eTask;
    }


    public ThreadedTaskRunner stop () {
        if (isRunning()) currentThreadedTask.stop();
        return this;
    }

    public ThreadedTaskRunner start () {
        setCurrentThreadedTask(newThreadedTask());
        new Thread(currentThreadedTask).start();
        return this;
    }

    public boolean isRunning (){
        return (currentThreadedTask != null) && currentThreadedTask.isRunning();
    }
}
