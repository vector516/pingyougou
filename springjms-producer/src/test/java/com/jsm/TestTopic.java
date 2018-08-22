package com.jsm;

import com.pinyougou.jms.producer.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jms-producer.xml")
public class TestTopic {
    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void testTopic(){
        topicProducer.sendTopicMessage("这是一个主题消息,发布订阅");
    }


}
