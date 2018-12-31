package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("goodsm")
@RestController
public class GoodsManagerController {
  private  String name = SecurityContextHolder.getContext().getAuthentication().getName();
    @Reference
    private GoodsService goodsService;

    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){

        goods.setSellerId(name);
      return   goodsService.search(page,rows,goods);
    }
    @RequestMapping("updateDelStatus")
    public Result updateDelStatus(Long[] ids, String dStatus){

        try {
            goodsService.updateDelStatus(ids,dStatus,name);
            return new Result(true,"成功");
        } catch (Exception e) {
            return new Result(true,"成功");
        }

    }
}
