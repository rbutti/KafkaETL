package com.rave.kafkaETL.dto;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.rave.kafkaETL.DbConnector;
import com.rave.kafkaETL.dbconnector.MySqlConnector;
import com.rave.kafkaETL.dto.KafkaData;
import com.rave.kafkaETL.dto.RunnerQueue;
import com.rave.kafkaETL.dto.kafkadata.EventData;
import com.rave.kafkaETL.dto.runnerqueue.MySqlRunnerQueue;
import com.rave.kafkaETL.runner.mysql.MySqlRunner;

import java.util.Collection;
import java.util.LinkedList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MySqlRunnerQueueSpec extends TestCase {

    String tableName = null;
    String database = null;
    DbConnector mySqlConnector = null;

    String username = "USER";
    String password = "PASSWORD";
    String host = "localhost";
    int port = 3306;

    @Before
    public void before() {
        this.tableName = "TEST_TABLE";
        this.database = "TEST_DB";
        this.mySqlConnector = new MySqlConnector(username, password, host, port);
    }

    @Test
    public void RunnerQueueTest() {
        EventData eventData = mock(EventData.class);
        MySqlRunner mySqlRunner = mock(MySqlRunner.class);

        when(eventData.mapToRunner(tableName, database, mySqlConnector)).thenReturn(mySqlRunner);

        /*
        *  init MySqlRunnerQueue and check isEmpty method
        * */
        RunnerQueue mySqlRunnerQueue = new MySqlRunnerQueue(tableName, database, mySqlConnector);
        assertEquals(true, mySqlRunnerQueue.isEmpty());

        /*
         *  enqueue the EventData collection and check dequeue method
         * */
        Collection<KafkaData> collection = new LinkedList<KafkaData>();
        collection.add(eventData);
        mySqlRunnerQueue.enqueue(collection);

        assertEquals(eventData.mapToRunner(tableName, database, mySqlConnector), mySqlRunnerQueue.dequeue());
    }
}
