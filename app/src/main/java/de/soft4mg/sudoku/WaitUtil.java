package de.soft4mg.sudoku;

import android.util.Log;

import java.util.Objects;

public class WaitUtil {

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static void doWait(Object object, long millis, String tag){

        try {
            synchronized (object){
                object.wait(millis);
            }
        } catch (InterruptedException e) {
            Log.w(tag, Objects.requireNonNull(e.getMessage()));
        }
    }
}
