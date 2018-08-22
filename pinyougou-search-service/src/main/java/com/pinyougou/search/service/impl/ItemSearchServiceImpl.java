package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
//        Query query = new SimpleQuery();
//        //添加查询条件
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows", page.getContent());

        //1.查询列表
        map.putAll(searchList(searchMap));

        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);


        //3.查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            //如果有分类名称,重新查询品牌和规格
            map.putAll(searchBrandAndSpecList(categoryName));

        } else {
            if (categoryList.size() > 0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }

    //查询到的商品高亮显示
    private Map searchList(Map searchMap) {
        Map map = new HashMap<>();
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions options = new HighlightOptions().addField("item_title");
        //高亮的配置
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(options);

        //1.1按照关键字查询
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(simpleFilterQuery);
        }

        //1.3按品牌筛选
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteria2 = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria2);
            query.addFilterQuery(simpleFilterQuery);
        }

        //1.4按规格删选
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria criteria3 = new Criteria("item_spec_" + key).is(specMap.get(key));
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria3);
                query.addFilterQuery(simpleFilterQuery);
            }

        }

        //1.5按价格筛选
        if (!"".equals(searchMap.get("price"))) {
            String[] price = ((String) searchMap.get("price")).split("-");
            if (!price[0].equals("0")) {
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")) {
                //如果区间终点不等于*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }


        //1.6 分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
        if (pageNo == null) {
            pageNo = 1;//默认第一页
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if (pageSize == null) {
            pageSize = 20;//默认20
        }
        query.setOffset((pageNo - 1) * pageSize);//从第几条记录查询
        query.setRows(pageSize);


        //1.7排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }


        }

        //高亮显示
        query.addCriteria(criteria);
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);

        for (HighlightEntry<TbItem> h : tbItems.getHighlighted()) {
            TbItem item = h.getEntity();
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //设置高亮的结果
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));

            }

        }

        map.put("rows", tbItems.getContent());

        map.put("totalPages", tbItems.getTotalPages());//返回总页数

        map.put("total", tbItems.getTotalElements());//返回总记录数

        return map;

    }


    //查询分类列表
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList<>();
        SimpleQuery simpleQuery = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        simpleQuery.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        simpleQuery.setGroupOptions(groupOptions);

        //得到分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(simpleQuery, TbItem.class);

        //根据列得到分组的结果集
        GroupResult<TbItem> groupResult = tbItems.getGroupResult("item_category");

        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }

        return list;

    }

    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        //获取模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表

            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;

    }


}
