package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.ContentCategory;
import cn.itcast.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  广告分类管理
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    /*广告分类 分页条件查询*/
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody ContentCategory contentCategory){
        PageResult result = contentCategoryService.search(page, rows, contentCategory);
        return result;
    }

    /*保存广告分类*/
    @RequestMapping("add")
    public Result add(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.add(contentCategory);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /*查询单个实例*/
    @RequestMapping("findOne")
    public ContentCategory findOne(Long id){
        return contentCategoryService.findOne(id);
    }

    /*更新广告分类*/
    @RequestMapping("update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try {
            contentCategoryService.update(contentCategory);
            return new Result(true,"修改成功");
        } catch (Exception e) {
           // e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /*批量删除广告分类*/
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            contentCategoryService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

   /*查询全部广告分类*/
   @RequestMapping("findAll")
    public List<ContentCategory> findAll(){
       return contentCategoryService.findAll();
   }
}
