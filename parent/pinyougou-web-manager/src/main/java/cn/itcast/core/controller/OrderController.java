package cn.itcast.core.controller;


import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("orders")
public class OrderController {

    @Reference
    OrderService orderService;
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Order order){
        return orderService.search(page,rows,order);
    }
    //SELECT SUM(payment),DATE(create_time) FROM tb_order   WHERE MONTH(create_Time)=(SELECT MONTH(CURDATE()))  GROUP BY DATE(create_time);
    @RequestMapping("creatPic")
    public List creatPic(){
        return orderService.creatPic();
    }
}
