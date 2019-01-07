package cn.itcast.core.controller;

import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户订单管理
 */
@RequestMapping("order")
@RestController
public class OrderController {
    @Reference
    private OrderService orderService;

    /*分页查询订单及订单详情*/
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String,String> searchMap) {
        //$scope.searchMap={'pageNo':1,'pageSize':3,'userId':''};
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        searchMap.put("userId",userId);
        return orderService.search(searchMap);
    }

   /*取消订单*/
   @RequestMapping("/cancleOrder")
    public Result cancleOrder(Long orderId){
       try {
           orderService.cancleOrder(orderId);
           return new Result(true,"订单取消成功!");

       }catch (Exception e){
           return new Result(false,"取消失败!");
       }
   }

}
