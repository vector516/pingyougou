package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pingyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;


    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }


    //分页功能的实现--->使用PageHelper类实现
    @Override
    public PageResult findPage(int pageNum, int size) {

        PageHelper.startPage(pageNum, size);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());

    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(int page, int rows, TbBrand tbBrand) {
        PageHelper.startPage(page, rows);

        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();

        if (tbBrand != null) {
            if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
                criteria.andNameLike("%" + tbBrand.getName() + "%");
            }

            if (tbBrand.getFirstChar() != null && tbBrand.getFirstChar().length() > 0) {
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }

        }

        Page<TbBrand> page2 = (Page<TbBrand>) brandMapper.selectByExample(example);
        PageResult pageResult = new PageResult(page2.getTotal(), page2.getResult());


        return pageResult;
    }

    @Override
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    @Override
    public void update(TbBrand tbBrand) {
//        TbBrandExample tbBrandExample = new TbBrandExample();
//        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
//        criteria.andIdEqualTo(tbBrand.getId());
//
//        //特别注意update的用法需要new XxxExample();
//        brandMapper.updateByExampleSelective(tbBrand,tbBrandExample);
        brandMapper.updateByPrimaryKey(tbBrand);
    }
}
