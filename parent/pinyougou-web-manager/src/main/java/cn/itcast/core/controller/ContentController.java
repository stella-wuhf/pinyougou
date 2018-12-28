package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  广告管理
 */
@RestController
@RequestMapping("content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /*广告分页条件查询*/
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Content content){
         return contentService.search(page,rows,content);
    }

    /*广告添加*/
    @RequestMapping("add")
    public Result add(@RequestBody Content content){
        try {
            contentService.add(content);
            return new Result(true,"广告添加成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"广告添加失败");
        }
    }

    /*查询单个广告实例*/
    @RequestMapping("findOne")
    public Content findOne(Long id){
        return contentService.findOne(id);
    }

    /*更新广告*/
    @RequestMapping("update")
    public Result update(@RequestBody Content content){
        try {
            contentService.update(content);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /*批量删除广告*/
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
