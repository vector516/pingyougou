package com.pinyougou.sellergoods.service;

import com.pingyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {
    public List<TbBrand> findAll();


    //分页的方法
    public PageResult findPage(int pageNum,int size);

    void add(TbBrand tbBrand);

    void delete(Long[] ids);

    PageResult search(int page, int rows, TbBrand tbBrand);

    TbBrand findOne(Long id);

    void update(TbBrand tbBrand);
}
