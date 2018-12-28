package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;

    /*分页查询-带条件*/
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate tt){
        return typeTemplateService.search(page,rows,tt);
    }

    /*增加模板*/
    @RequestMapping("add")
    public Result add(@RequestBody TypeTemplate tt){
        try {
            typeTemplateService.add(tt);
            return new Result(true,"保存成功");
        } catch (Exception e) {
           // e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /*修改模板*/
    @RequestMapping("update")
    public Result update(@RequestBody TypeTemplate tt){
        try {
            typeTemplateService.update(tt);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            // e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /*查询一个模板*/
    @RequestMapping("findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    /*删除模板*/
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            // e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /*根据模板id查询规格列表,返回值是List<Map>*/
    @RequestMapping("findBySpecList")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }

}
