package com.rave.kafkaETL.consumer;

import java.util.HashMap;

import com.rave.kafkaETL.Runner;
import com.rave.kafkaETL.dto.KafkaData;

public abstract class ConsumerRunner extends Runner {
    public abstract void run();

    public abstract HashMap<String, KafkaData> setHashMap(HashMap<String, KafkaData> hashMap);

    public abstract HashMap<String, KafkaData> getHashMap();
}
