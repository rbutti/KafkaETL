package com.rave.kafkaETL.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rave.kafkaETL.DbConnector;
import com.rave.kafkaETL.Runner;
import com.rave.kafkaETL.Service;
import com.rave.kafkaETL.consumer.ConsumerRunner;
import com.rave.kafkaETL.consumer.kafkaconsumer.KafkaConsumeRunner;
import com.rave.kafkaETL.dbconnector.MySqlConnector;
import com.rave.kafkaETL.dto.RunnerQueue;
import com.rave.kafkaETL.dto.runnerqueue.MySqlRunnerQueue;
import com.rave.kafkaETL.runner.intervaltime.IntervalTimeRunner;
import com.rave.kafkaETL.runner.managequeue.QueueManagingRunner;

public final class KafkaToMysqlService implements Service {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private String tableName = null;
    private String databse = null;

    private DbConnector mySqlConnector = null;
    private ConsumerRunner kafkaConsumeRunner = null;
    private Runner intervalTimeRunner = null;
    private RunnerQueue mySqlRunnerQueue = null;
    private ExecutorService executorService = null;

    private final Lock mutex = new ReentrantLock(true);

    public KafkaToMysqlService(String PROPERTIES_PATH) {
        try {
            File file = new File(PROPERTIES_PATH);
            InputStream input = new FileInputStream(file);
            Properties props = new Properties();
            props.load(input);
            this.mySqlConnector = initMySqlConnector(props);
            this.kafkaConsumeRunner = initKafkaConsumeRunner(props);
            this.tableName = props.getProperty("db.table_name");
            this.databse = props.getProperty("db.database");

            int threadPoolSize = 5;
            try {
                int poolSize = Integer.valueOf(props.getProperty("maxThreadPoolSize"));
                if (poolSize > 5) {
                    threadPoolSize = poolSize;
                }
            } catch (NullPointerException e) {
                // catch but nothing to do
            } finally {
                this.executorService = Executors.newFixedThreadPool(threadPoolSize);
            }

            this.mySqlRunnerQueue = new MySqlRunnerQueue(
                    tableName,
                    databse,
                    mySqlConnector
            );
            this.intervalTimeRunner = initIntervalTimeRunner(props);
            input.close();
        } catch (IOException ie) {
            ie.printStackTrace();
            System.exit(1);
        }
    }

    private Runner initIntervalTimeRunner(Properties props) {
        long interval = Long.valueOf(props.getProperty("interval.min"));

        return new IntervalTimeRunner(interval, mySqlRunnerQueue, mutex, kafkaConsumeRunner);
    }

    private ConsumerRunner initKafkaConsumeRunner(Properties props) {
        List<String> topics = Arrays.asList(props.getProperty("consumer.topics").trim().split(","));
        String bootstrapServers = props.getProperty("consumer.bootstrap_servers");
        String groupId = props.getProperty("consumer.group_id");

        return new KafkaConsumeRunner(topics, bootstrapServers, groupId, mutex);
    }

    private DbConnector initMySqlConnector(Properties props) {
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        String host = props.getProperty("db.host");
        int port = Integer.valueOf(props.getProperty("db.port"));
        return new MySqlConnector(username, password, host, port);
    }

    public Service start() {
        executorService.execute(kafkaConsumeRunner);
        executorService.execute(intervalTimeRunner);
        executorService.execute(new QueueManagingRunner(mySqlRunnerQueue, executorService));
        logger.log(Level.INFO, "Services STARTED");
        return this;
    }

    public void shutdown() {
        executorService.shutdown();
        logger.log(Level.INFO, "Shutdown Service...");
    }
}
