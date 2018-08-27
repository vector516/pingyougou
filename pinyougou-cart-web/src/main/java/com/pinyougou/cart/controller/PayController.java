package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制层
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/pay")
public class PayController {


    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;


    //生成微信支付的二维码
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询支付日志
        TbPayLog payLog  = orderService.searchPayLogFromRedis(userId);
        if(payLog!=null){
            Map map = weixinPayService.createNative(payLog.getOutTradeNo(), "1");
            return map;
        }else {

            return new HashMap();
        }


    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int x=0;
        while (true) {
            //调用查询接口
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                //支付出错
                result = new Result(false, "支付出错");
                break;
            }

            if (map.get("trade_state").equals("SUCCESS")) {
                //如果成功
                result = new Result(true, "支付成功");

                //修改订单状态,修改支付日志,清除redis缓存
                orderService.updateOrderStatus(out_trade_no,  map.get("transaction_id"));

                break;
            }

            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if(x>=5){
                result=new  Result(false, "二维码超时");
                break;
            }

        }
        return result;
    }


}
