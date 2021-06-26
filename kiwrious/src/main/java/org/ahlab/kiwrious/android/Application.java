package org.ahlab.kiwrious.android;

import android.app.Activity;
import android.content.Context;

public class Application extends android.app.Application {

    /** Instance of the current application. */
    private static Context instance;

    /**
     * Constructor.
     */
    public Application(Context context) {
        instance = context;
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
