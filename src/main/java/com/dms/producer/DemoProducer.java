package com.dms.producer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.dms.consumer.DemoConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class DemoProducer<K, V> {

    public static final String CONFIG_PRODUCER_FILE_NAME = "producer.properties";

    private Producer<K, V> producer;

    DemoProducer(String path) {
        Properties props = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(path))) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        producer = new KafkaProducer<>(props);
    }

    DemoProducer() {
        Properties props = new Properties();
        try {
            props = loadFromClasspath(CONFIG_PRODUCER_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        producer = new KafkaProducer<>(props);
    }

    /**
     * 生产消息
     *
     * @param topic     topic对象
     * @param partition partition
     * @param key       消息key
     * @param data      消息数据
     */
    public void produce(String topic, Integer partition, K key, V data) {
        produce(topic, partition, key, data, null, (Callback) null);
    }

    /**
     * 生产消息
     *
     * @param topic     topic对象
     * @param partition partition
     * @param key       消息key
     * @param data      消息数据
     * @param timestamp timestamp
     */
    public void produce(String topic, Integer partition, K key, V data, Long timestamp) {
        produce(topic, partition, key, data, timestamp, (Callback) null);
    }

    /**
     * 生产消息
     *
     * @param topic     topic对象
     * @param partition partition
     * @param key       消息key
     * @param data      消息数据
     * @param callback  callback
     */
    public void produce(String topic, Integer partition, K key, V data, Callback callback) {
        produce(topic, partition, key, data, null, callback);
    }

    public void produce(String topic, V data) {
        produce(topic, null, null, data, null, (Callback) null);
    }

    public void produce(String topic, V data, Callback callback) {
        produce(topic, null, null, data, null, callback);
    }

    /**
     * 生产消息
     *
     * @param topic     topic对象
     * @param partition partition
     * @param key       消息key
     * @param data      消息数据
     * @param timestamp timestamp
     * @param callback  callback
     */
    public void produce(String topic, Integer partition, K key, V data, Long timestamp, Callback callback) {
        ProducerRecord<K, V> kafkaRecord =
                timestamp == null ? new ProducerRecord<>(topic, partition, key, data)
                        : new ProducerRecord<>(topic, partition, timestamp, key, data);
        produce(kafkaRecord, callback);
    }

    public void produce(ProducerRecord<K, V> kafkaRecord) {
        produce(kafkaRecord, (Callback) null);
    }

    public void produce(ProducerRecord<K, V> kafkaRecord, Callback callback) {
        producer.send(kafkaRecord, callback);
    }

    public void close() {
        producer.close();
    }

    /**
     * get classloader from thread context if no classloader found in thread
     * context return the classloader which has loaded this class
     *
     * @return classloader
     */
    public static ClassLoader getCurrentClassLoader() {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null) {
            classLoader = DemoProducer.class.getClassLoader();
        }
        return classLoader;
    }

    /**
     * 从classpath 加载配置信息
     *
     * @param configFileName 配置文件名称
     * @return 配置信息
     * @throws IOException
     */
    public static Properties loadFromClasspath(String configFileName) throws IOException {
        return DemoConsumer.getProperties(configFileName, getCurrentClassLoader());
    }
}
