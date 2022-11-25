package de.soft4mg.sudoku;

import android.util.Log;

public class WaitUtil {

    public static void doWait(Object object, long millis, String tag){

        try {
            synchronized (object){
                object.wait(millis);
            }
        } catch (InterruptedException e) {
            Log.w(tag, e.getMessage() );
        }
    }
}
