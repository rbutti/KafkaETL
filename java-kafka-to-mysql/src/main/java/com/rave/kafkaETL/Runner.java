package com.rave.kafkaETL;

public abstract class Runner implements Runnable {
    public abstract void run();

    public void close() { this.close(); }
}
