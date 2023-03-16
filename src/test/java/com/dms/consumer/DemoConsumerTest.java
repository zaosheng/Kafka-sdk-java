package com.dms.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

import java.util.Collections;

public class DemoConsumerTest {
    @Test
    public void testConsumer() throws Exception {
        DemoConsumer consumer = new DemoConsumer();
        consumer.consume(Collections.singletonList("test"));
        try {
            for (int i = 0; i < 10; i++) {
                ConsumerRecords<Object, Object> records = consumer.poll(1000);
                System.out.println("the message numbers of topic:" + records.count());
                for (ConsumerRecord<Object, Object> record : records) {
                    System.out.println(record.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
