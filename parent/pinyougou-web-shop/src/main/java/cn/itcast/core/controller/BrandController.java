package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brandst;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("search")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Brandst brandst){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        brandst.setSellerId(name);
        return brandService.searchStaus(pageNum,pageSize,brandst);
    }
    @RequestMapping("add")
    public Result add(@RequestBody Brandst brandst){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        brandst.setSellerId(name);
        try {
            brandService.addStaus(brandst);
            System.out.println();
            return new Result(true,"操作成功");
        } catch (Exception e) {
            return new Result(false,"操作失败");
        }
    }
    /*删除品牌*/
    @RequestMapping("dele")
    public Result delestu(Long[] ids){
        try {
            brandService.delestu(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    /*查询单个品牌信息,根据id*/
    @RequestMapping("findOne")
    public Brandst findOne(Long id){
        return brandService.findOnest(id);
    }

    /*更新品牌*/
    @RequestMapping("update")
    public Result update(@RequestBody Brandst brandst){
        try {
            brandService.updatest(brandst);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }
    /*查询全部品牌列表,返回时一个list<map>*/
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return brandService.selectOptionListSt(name);
    }
}
