package sample;

import sun.awt.Mutex;

import java.util.ArrayList;

/**
 * @author - Jonas Funcke
 * @param <T> - Type for ArrayList
 */
public class ConcurrentArrayList<T> extends ArrayList<T> {
    private Mutex mutex = new Mutex();

    public void lock() {
        this.mutex.lock();
    }

    public void unlock() {this.mutex.unlock();}
}
