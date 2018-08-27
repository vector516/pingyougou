package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    //微信公众账号或开放平台APP的唯一标识
    @Value("${appid}")
    private String appid;

    //财付通平台的商户账号
    @Value("${partner}")
    private String partner;

    //财付通平台的商户密钥
    @Value("${partnerkey}")
    private String partnerkey;

    //回调地址

    /**
     * 生成微信支付二维码
     *
     * @param out_trade_no 订单号
     * @param total_fee    金额(分)
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String, String> param = new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", out_trade_no);//商户订单号
        param.put("total_fee", total_fee);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型

        //2.生成要发送的xml
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            //微信支付的下单接口--->HttpClient模拟浏览器请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //3.获得结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> map = new HashMap<>();
            //支付地址
            map.put("code_url", resultMap.get("code_url"));
//            map.put("code_url", "http://www.baidu.com");
            //总金额
            map.put("total_fee", total_fee);
            //订单号
            map.put("out_trade_no", out_trade_no);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    /**
     * 支付状态查询
     * @param out_trade_no
     * @return
     */

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        //关闭支付的微信接口
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        String xmlParam = null;
        try {
            xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }






    }
}
