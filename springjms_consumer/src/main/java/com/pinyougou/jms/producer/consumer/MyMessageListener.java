package com.pinyougou.jms.producer.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {




    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            System.out.println("接收到消息是:"+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
