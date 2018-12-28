package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /*查询所有品牌数据*/
    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /*分页查询品牌数据列表, 无条件查询*/
    @RequestMapping("findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        return brandService.findPage(pageNum, pageSize);
    }

    /*分页查询品牌数据列表 , 条件查询*/
    @RequestMapping("search")
    public PageResult search(Integer pageNum, Integer pageSize,@RequestBody Brand brand) {
        return brandService.search(pageNum, pageSize,brand);
    }

    /*新增品牌*/
    @RequestMapping("add")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    /*查询单个品牌信息,根据id*/
    @RequestMapping("findOne")
    public Brand findOne(Long id){
      return brandService.findOne(id);
    }

    /*更新品牌*/
    @RequestMapping("update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    /*删除品牌*/
    @RequestMapping("dele")
    public Result dele(Long[] ids){
        try {
            brandService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /*查询全部品牌列表,返回时一个list<map>*/
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}


