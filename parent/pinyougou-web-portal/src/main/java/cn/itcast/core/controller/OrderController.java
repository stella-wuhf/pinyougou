package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /*保存订单  包括订单主表和订单详情表*/
    @RequestMapping("add")
    public Result add(@RequestBody Order order){
        try {
            //先给订单设置用户id,即用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(name);
            orderService.add(order);
            return new Result(true,"提交订单成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交订单失败!");
        }
    }
}
