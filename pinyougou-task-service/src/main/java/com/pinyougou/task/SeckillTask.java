package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;


    /**
     * 每分钟刷新秒杀商品
     */
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println("执行了任务调度" + new Date());
        //查询所有的秒杀商品键集合
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());

        //查询正在秒杀的商品列表
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//剩余库存大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
        //排除缓存中已经有的商品
        criteria.andIdNotIn(ids);

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        //将新增的秒杀商品装入缓存中
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
        }
        System.out.println("将" + seckillGoodsList.size() + "条商品装入缓存");

    }

    /**
     * 过期移除秒杀商品
     */
    @Scheduled(cron="* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("移除秒杀商品任务在执行");

        //扫描缓存中秒杀商品列表，发现过期的移除
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        for( TbSeckillGoods seckill:seckillGoodsList ){
            if(seckill.getEndTime().getTime()<((new Date()).getTime())){
                (new Date()).getTime();
                //如果结束日期小于当前日期，则表示过期
                //向数据库保存记录
                seckillGoodsMapper.updateByPrimaryKey(seckill);
                //移除缓存数
                redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());
                System.out.println("移除秒杀商品"+seckill.getId());
            }

        }
        System.out.println("移除秒杀商品任务结束");

    }





}
