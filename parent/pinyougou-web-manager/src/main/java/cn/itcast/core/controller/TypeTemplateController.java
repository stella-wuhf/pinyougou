package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateSt;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/searchst")
    public PageResult searchst(Integer page, Integer rows, @RequestBody TypeTemplateSt typeTemplateSt){
        return typeTemplateService.searchst(page,rows,typeTemplateSt);
    }
    /*修改商品状态  审核状态*/
    @RequestMapping("updateStatusst")
    public Result updateStatusst(Long[] ids,String status){
        try {
            typeTemplateService.updateStatusst(ids,status);
            return new Result(true,"修改状态成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"修改状态失败");
        }
    }

}
