package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息...");
        TextMessage textMessage=(TextMessage)message;
        try {
            String text = textMessage.getText();
            List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
        //循环的目的是为了将spec的json字符串解析为map格式并存入原对象中,便于存到solor后的检索
            for(TbItem item:tbItems){
                System.out.println(item.getId()+"   "+item.getTitle());
                //将spec字段中的json字符串转换为map
                Map specMap= JSON.parseObject(item.getSpec());
                item.setSpecMap(specMap);
            }
            itemSearchService.importList(tbItems);
            System.out.println("成功导入到索引库");

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
