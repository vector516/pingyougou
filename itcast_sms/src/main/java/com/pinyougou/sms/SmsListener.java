package com.pinyougou.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map)  {
        SendSmsResponse response = null;
        System.out.println("接收到"+map);
        try {
            response = smsUtil.sendSms(map.get("mobile"), map.get("template_code"), map.get("sign_name"), map.get("param"));
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
