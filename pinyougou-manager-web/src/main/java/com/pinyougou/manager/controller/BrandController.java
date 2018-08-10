package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pingyougou.entity.PageResult;
import com.pingyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    //返回品牌的全部列表
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {

        return brandService.findAll();

    }


    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        PageResult pageResult = brandService.findPage(page, rows);
        return pageResult;
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand) {
        try {
            brandService.add(tbBrand);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }

    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }

    }

    //查询并分页的功能
    @RequestMapping("/search")
    public PageResult search(int page, int rows, @RequestBody TbBrand tbBrand) {

        PageResult result = brandService.search(page, rows, tbBrand);
        return result;
    }

    //修改功能的数据回显,根据id查数据
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = brandService.findOne(id);
        return tbBrand;
    }

    //修改数据之保存功能的实现
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){

        try {
            //注意修改的方法为updatByPrimaryKey(TbBrand tbBrand)
            brandService.update(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }


}
