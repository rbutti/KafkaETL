package com.rave.kafkaETL.runner;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.rave.kafkaETL.consumer.kafkaconsumer.KafkaConsumeRunner;
import com.rave.kafkaETL.dto.KafkaData;
import com.rave.kafkaETL.dto.runnerqueue.MySqlRunnerQueue;
import com.rave.kafkaETL.runner.intervaltime.IntervalTimeRunner;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IntervalTimeRunnerSpec extends TestCase {

    private IntervalTimeRunner intervalTimeRunner = null;

    private Lock mutex = new ReentrantLock(true);
    private long interval = 10;

    @Test
    public void IntervalTimeRunnerTest() {

        MySqlRunnerQueue mySqlRunnerQueue = mock(MySqlRunnerQueue.class);
        KafkaConsumeRunner kafkaConsumeRunner = mock(KafkaConsumeRunner.class);

        /*
        *  Check intervalTimeRunner hashMap and kafkaConsumeRunner hashMap
        *  when intervalTime expired hashMap reset
        * */

        intervalTimeRunner = new IntervalTimeRunner(interval, mySqlRunnerQueue, mutex, kafkaConsumeRunner);
        HashMap<String, KafkaData> newHashMap = new HashMap<String, KafkaData>();

        when(kafkaConsumeRunner.setHashMap(newHashMap)).thenReturn(newHashMap);

        intervalTimeRunner.resetHashMap(newHashMap);

        assertEquals(intervalTimeRunner.getHashMap(), kafkaConsumeRunner.getHashMap());
    }
}
