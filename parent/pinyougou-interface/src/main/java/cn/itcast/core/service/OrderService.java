package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;

import java.util.Map;

public interface OrderService {
    void add(Order order);

    Map<String, Object> search(Map<String, String> searchMap);

    void cancleOrder(Long orderId);
}
