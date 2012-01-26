package com.box.androidlib.Utils;

/**
 * Interface representing a cancelable asynchronous task.
 * 
 */
public interface Cancelable {

    /**
     * Cancel the task.
     * 
     * @return True if successfully canceled, false otherwise.
     */
    boolean cancel();

}
