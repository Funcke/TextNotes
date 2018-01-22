package sample;

import sun.awt.Mutex;

import java.util.ArrayList;

public class ConcurrentArrayList<T> extends ArrayList<T> {
    private Mutex mutex;

    /*public ConcurrentArrayList<T>() {
        super<T>();
        this.mutex = new Mutex();
    }*/

    public void lock() {
        this.mutex.lock();
    }
}
