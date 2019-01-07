package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀管理
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /*秒杀商品申请*/
    @RequestMapping("/add")
    public Result add(@RequestBody SeckillGoods seckillGoods){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(sellerId);
        try {
            seckillGoodsService.add(seckillGoods);
            return new Result(true,"秒杀商品提交申请成功");
        }catch (Exception e){
            return new Result(false,"提交申请失败");
        }
    }

    /*秒杀商品分页查询*/
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods seckillGoods){
        return seckillGoodsService.search(page,rows,seckillGoods);
    }
}
