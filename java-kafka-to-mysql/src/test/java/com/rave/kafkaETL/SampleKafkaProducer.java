package com.rave.kafkaETL;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.rave.kafkaETL.dbconnector.MySqlConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SampleKafkaProducer extends Thread {

	private Properties properties = new Properties();
	private KafkaProducer<Integer, String> kafkaProducer = null;
	private String topic = null;
	private DbConnector mySqlConnector = null;
	private String tableName;
	private String database;

	public SampleKafkaProducer(String topic, String propertiesPath) {

		try {
			if (topic == null) {
				throw new IllegalArgumentException("topic must not be null value");
			} else {
				this.topic = topic;
				properties.put("bootstrap.servers", "localhost:9092");
				properties.put("client.id", "DemoProducer");
				properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
				properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				kafkaProducer = new KafkaProducer<Integer, String>(properties);
			}

			File file = new File(propertiesPath);
			InputStream input = new FileInputStream(file);
			Properties props = new Properties();
			props.load(input);
			this.mySqlConnector = initMySqlConnector(props);

			this.tableName = props.getProperty("db.input_table_name");
			this.database = props.getProperty("db.database");

		} catch (IOException ie) {
			ie.printStackTrace();
			System.exit(1);
		}
	}

	

	private DbConnector initMySqlConnector(Properties props) {
		String username = props.getProperty("db.username");
		String password = props.getProperty("db.password");
		String host = props.getProperty("db.host");
		int port = Integer.valueOf(props.getProperty("db.port"));
		return new MySqlConnector(username, password, host, port);
	}

	public String getInsertQuery() {
		String prefix = String.format("select * from %s.%s", this.database, this.tableName);

		return prefix;
	}

	public String createMsg(ResultSet rs) throws SQLException {
		return String.format("{event_id: %d, event_timestamp: '%s', service_code: '%s', event_context: '%s'}", rs.getInt("event_id"),
				 rs.getDate("event_timestamp"), rs.getString("service_code"), rs.getString("event_context"));
	}

	public void run() {
		Random random = new Random();
		ResultSet rs = mySqlConnector.executeQuery(getInsertQuery());
		try {
			while (rs.next()) {
				try {
					Thread.sleep(1000);
					kafkaProducer.send(new ProducerRecord<Integer, String>(topic, rs.getInt("event_id"), createMsg(rs))).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
