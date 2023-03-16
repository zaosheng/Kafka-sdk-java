package com.dms.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;

public class DemoProducerTest {
    @Test
    public void testProducer() {
        DemoProducer<String, String> producer = new DemoProducer<>();

        try {
            for (int i = 0; i < 10; i++){
                String data = "The msg is " + i;
                producer.produce("test", data, new Callback()
                {
                    public void onCompletion(RecordMetadata metadata,
                                             Exception exception)
                    {
                        if (exception != null)
                        {
                            exception.printStackTrace();
                            return;
                        }
                    }
                });
                System.out.println("produce msg:" + data);
            }
        }catch (Exception e)
        {
            // TODO: 异常处理
            e.printStackTrace();
        }finally {
            producer.close();
        }
    }
}
