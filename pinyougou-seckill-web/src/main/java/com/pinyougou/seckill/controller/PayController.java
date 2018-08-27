package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 60000)
    private WeixinPayService weixinPayService;

    @Reference(timeout = 60000)
    private SeckillOrderService seckillOrderService;


    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);

        if (seckillOrder != null) {
            //将需要支付的金额转换为分---->微信支付的单位为分
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);
//            Map map = weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
            Map map = weixinPayService.createNative(seckillOrder.getId() + "",  "1");
            return map;
        } else {
            return new HashMap();
        }

    }





    /**
     * 查询支付状态
     * @param out_trade_no 订单号
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result=null;
        int x=0;
        while (true){
            //调用查询接口查询支付状态
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                result=new Result(false,"支付出错");
                break;
            }

            if(map.get("trade_state").equals("SUCCESS")){
                //支付成功
                result=new Result(true,"支付成功");

                //支付成功需要把订单从redis中的订单保存到数据库
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no),map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x++;
            if(x>10){
                result=new Result(false,"二维码超时");

                //调用微信超时未支付的接口
                Map<String,String> payresult = weixinPayService.closePay(out_trade_no);
                if( !"SUCCESS".equals(payresult.get("result_code")) ){
                    //如果返回结果是正常关闭
                    if("ORDERPAID".equals(payresult.get("err_code"))){
                        result=new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }

                }
                if(result.isSuccess()==false){
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }


        }
        return result;
    }







}
