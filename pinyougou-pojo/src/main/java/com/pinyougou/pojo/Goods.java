package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {
    private TbGoods goods;//spu--->商品种类
    private TbGoodsDesc goodsDesc;//商品详情
    private List<TbItem> itemList;//商品的sku--->最小库存单位

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
