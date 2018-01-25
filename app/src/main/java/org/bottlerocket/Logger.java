package org.bottlerocket;

import android.util.Log;

/**
 * Created by Himanshu on 1/22/2018.
 */

public class Logger {
    public static void log_d(String message){
        Log.d(new Exception().getStackTrace()[1].getClassName(),message);
    }
}
