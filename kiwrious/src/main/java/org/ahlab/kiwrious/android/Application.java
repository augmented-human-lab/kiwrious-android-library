package org.ahlab.kiwrious.android;

import android.content.Context;

public class Application extends android.app.Application {

    /** Instance of the current application. */
    private static Application instance;

    /**
     * Constructor.
     */
    public Application() {
        instance = this;
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

}
