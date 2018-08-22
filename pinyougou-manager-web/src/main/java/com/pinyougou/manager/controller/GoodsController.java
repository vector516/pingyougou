package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.entity.PageResult;
import com.pingyougou.entity.Result;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.Goods;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemSearchService itemSearchService;

    @Reference(timeout = 40000)
    private ItemPageService itemPageService;

    /**
     * 生成静态页（测试）
     *
     * @param goodsId
     */
    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId) {
        itemPageService.genItemHtml(goodsId);
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        //获取权限名
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        //将商品与商家的id进行关联
        goods.getGoods().setSellerId(sellerId);


        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            //删除商品时更新solr服务器上的数据
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    /**
     * 逻辑删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteByLogic")
    public Result deleteByLogic(Long[] ids) {
        try {
            goodsService.deleteByLogic(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {

        try {
            goodsService.updateStatus(ids, status);

            if (status.equals("1")) {
                //查询每个商品的sku数据
                List<TbItem> tbItems = goodsService.findItemListByGoodsIdandStatus(ids, status);
                //保存到solr服务器上

                if (tbItems.size() > 0) {
                    itemSearchService.importList(tbItems);
                } else {
                    System.out.println("没有明细数据");
                }

                //商品审核成功生成静态页生成
                for(Long goodsId:ids){
                    itemPageService.genItemHtml(goodsId);
                }

            }
            return new Result(true, "审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }

    }


}
