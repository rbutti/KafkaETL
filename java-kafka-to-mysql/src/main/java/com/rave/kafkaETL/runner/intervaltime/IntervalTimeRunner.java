package com.rave.kafkaETL.runner.intervaltime;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

import com.rave.kafkaETL.Runner;
import com.rave.kafkaETL.consumer.ConsumerRunner;
import com.rave.kafkaETL.dto.KafkaData;
import com.rave.kafkaETL.dto.RunnerQueue;

public final class IntervalTimeRunner extends Runner {

    private long interval = 10;
    private RunnerQueue mySqlRunnerQueue = null;
    private Lock mutex = null;
    private HashMap<String, KafkaData> hashMap = null;
    private ConsumerRunner kafkaConsumeRunner = null;

    public IntervalTimeRunner(long interval,
                              RunnerQueue mySqlRunnerQueue,
                              Lock mutex,
                              ConsumerRunner kafkaConsumeRunner) {
        if (mySqlRunnerQueue == null || mutex == null || kafkaConsumeRunner == null) {
            throw new IllegalArgumentException("Runner Queue, Mutex or KafkaConsumeRunner must not be null value");
        } else {
            this.interval = interval;
            this.mySqlRunnerQueue = mySqlRunnerQueue;
            this.mutex = mutex;
            this.hashMap = kafkaConsumeRunner.getHashMap();
            this.kafkaConsumeRunner = kafkaConsumeRunner;
        }
    }

    public HashMap<String, KafkaData> getHashMap() {
        return this.hashMap;
    }

    public Collection<KafkaData> resetHashMap(HashMap<String, KafkaData> newHashMap) {

        /**
         * @param newHashMap: to init hashMap
         * @return old hashMap values Collections
         * */

        HashMap<String, KafkaData> copyHashMap = this.hashMap;
        mutex.lock();
        this.hashMap = newHashMap;
        mutex.unlock();
        kafkaConsumeRunner.setHashMap(this.hashMap);
        return copyHashMap.values();
    }

    public void run() {
        while(!Thread.interrupted()) {
            try {
                Thread.sleep(interval * 60 * 1000);
                Collection<KafkaData> collection = resetHashMap(new HashMap<String, KafkaData>());
                mySqlRunnerQueue.enqueue(collection);
            } catch (InterruptedException ie) {
                this.close();
            }
        }
    }
}
