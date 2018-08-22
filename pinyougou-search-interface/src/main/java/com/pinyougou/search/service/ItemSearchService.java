package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {


    /**
     * 搜索
     * @param keywords
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 将数据导入solr服务器
     * @param list
     */
    public void importList(List list);

    /**
     * 商品删除同步到solr
     * @param ids
     */
    public void deleteByGoodsIds(List goodsIdList);

}
