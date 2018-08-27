package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.entity.PageResult;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.insert(seckillGoods);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }

        }

        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 返回正在秒杀的列表
     *
     * @return
     */
    @Override
    public List<TbSeckillGoods> findList() {
        //从reids中获取秒杀商品列表
        List<TbSeckillGoods> tbSeckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        if(tbSeckillGoodsList==null||tbSeckillGoodsList.size()==0){

            //redis中没有秒杀数据,则第一次从数据库查询
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            //剩余库存大于0
            criteria.andStockCountGreaterThan(0);
            //开始时间小于等于当前时间
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //结束时间大于当前时间
            criteria.andEndTimeGreaterThan(new Date());

             tbSeckillGoodsList = seckillGoodsMapper.selectByExample(example);

            //秒杀商品数据存入redis
            for(TbSeckillGoods tbSeckillGoods:tbSeckillGoodsList){
                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(),tbSeckillGoods);
            }

        }



        return tbSeckillGoodsList;


//		List<TbSeckillGoods> seckillGoodsList =	redisTemplate.boundHashOps("seckillGoods").values();
//		if(seckillGoodsList==null || seckillGoodsList.size()==0){
//			TbSeckillGoodsExample example=new TbSeckillGoodsExample();
//			Criteria criteria = example.createCriteria();
//			criteria.andStatusEqualTo("1");// 审核通过的商品
//			criteria.andStockCountGreaterThan(0);//库存数大于0
//			criteria.andStartTimeLessThanOrEqualTo(new Date());//开始日期小于等于当前日期
//			criteria.andEndTimeGreaterThanOrEqualTo(new Date());//截止日期大于等于当前日期
//			seckillGoodsList = seckillGoodsMapper.selectByExample(example);
//			//将列表数据装入缓存
//			for(TbSeckillGoods seckillGoods:seckillGoodsList){
//				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
//			}
//			System.out.println("从数据库中读取数据装入缓存");
//		}else{
//			System.out.println("从缓存中读取数据");
//
//		}
//		return seckillGoodsList;

    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }

}
