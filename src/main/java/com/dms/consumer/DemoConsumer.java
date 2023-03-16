package com.dms.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class DemoConsumer {

    public static final String CONFIG_CONSUMER_FILE_NAME = "consumer.properties";

    private KafkaConsumer<Object, Object> consumer;

    DemoConsumer(String path) {
        Properties props = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(path))) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        consumer = new KafkaConsumer<>(props);
    }

    DemoConsumer() {
        Properties props = new Properties();
        try {
            props = loadFromClasspath(CONFIG_CONSUMER_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        consumer = new KafkaConsumer<>(props);
    }

    public void consume(List<String> topics) {
        consumer.subscribe(topics);
    }

    public ConsumerRecords<Object, Object> poll(long timeout) {
        return consumer.poll(timeout);
    }

    public void close() {
        consumer.close();
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
            classLoader = DemoConsumer.class.getClassLoader();
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
        return getProperties(configFileName, getCurrentClassLoader());
    }

    public static Properties getProperties(String configFileName, ClassLoader currentClassLoader) throws IOException {
        Properties config = new Properties();

        List<URL> properties = new ArrayList<>();
        Enumeration<URL> propertyResources = currentClassLoader.getResources(configFileName);
        while (propertyResources.hasMoreElements()) {
            properties.add(propertyResources.nextElement());
        }

        for (URL url : properties) {
            InputStream is = null;
            try {
                is = url.openStream();
                config.load(is);
            } finally {
                if (is != null) {
                    is.close();
                    is = null;
                }
            }
        }

        return config;
    }
}
