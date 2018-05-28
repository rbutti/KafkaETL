package com.rave.kafkaETL.dto;

import java.util.Collection;

import com.rave.kafkaETL.Runner;

public abstract class RunnerQueue {

    public abstract boolean isEmpty();

    public abstract Runner dequeue();

    public abstract void enqueue(Collection<KafkaData> collection);
}
