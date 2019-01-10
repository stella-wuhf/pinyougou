package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface OrderService {
    void add(Order order);

    Map<String, Object> search(Map<String, String> searchMap);

    void cancleOrder(Long orderId);
	PageResult search(Integer page, Integer rows, Order order);

    List creatPic();
}
