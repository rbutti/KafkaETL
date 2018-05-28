package com.rave.kafkaETL.dto;

import com.rave.kafkaETL.DbConnector;
import com.rave.kafkaETL.Runner;

public abstract class KafkaData {

    public abstract Runner mapToRunner(String tableName, String database, DbConnector connector);
}
