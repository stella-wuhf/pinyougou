package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.Cart;
import pojogroup.OrderVo;



import java.math.BigDecimal;
import java.util.*;

/**
 * 订单管理
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayLogDao payLogDao;

    //保存订单
    @Override
    public void add(Order order) {
        //支付总金额,此处定义的是元,实际接收的是分
        double totalFee = 0;
        //支付 订单编号列表
        List<String> ids = new ArrayList<>();

        //1. 从缓存中取出购物车结果集
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());
        // 遍历购物车结果集
        for (Cart cart : cartList) { //每一个购物车就是一个订单
            //订单id
            long orderId = idWorker.nextId();
            order.setOrderId(orderId);
            //保存订单id到支付的订单编号列表中
            ids.add(String.valueOf(orderId));
            //创建时间
            order.setCreateTime(new Date());
            //订单更新时间
            order.setUpdateTime(new Date());
            //商家ID
            order.setSellerId(cart.getSellerId());
            //订单状态, 1、未付款
            order.setStatus("1");
            //订单来源,2：pc端
            order.setSourceType("2");
            //订单实付金额
            double payment = 0;
            //购物车获取商品结果集

            List<OrderItem> orderItemList = cart.getOrderItemList();
            //遍历商品结果集
            for (OrderItem orderItem : orderItemList) {//每一个商品就是订单里的一个订单详情
                //通过库存id查询到库存对象
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                //订单详情id
                long orderItemId = idWorker.nextId();
                orderItem.setId(orderItemId);
                //商品id
                orderItem.setGoodsId(item.getGoodsId());
                //订单id
                orderItem.setOrderId(orderId);
                //商品标题
                orderItem.setTitle(item.getTitle());
                //商品单价
                orderItem.setPrice(item.getPrice());
                //商品图片地址
                orderItem.setPicPath(item.getImage());
                //商家id
                orderItem.setSellerId(item.getSellerId());
                //商品总金额,小计
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));
                //订单总金额
                payment += orderItem.getTotalFee().doubleValue();
                //保存订单详情
                orderItemDao.insertSelective(orderItem);
            }
            //设置订单总金额
            order.setPayment(new BigDecimal(payment));
            //保存订单
            orderDao.insertSelective(order);
            //设置支付总金额
            totalFee += order.getPayment().doubleValue();
        }
        //都保存完后,清除缓存中的购物车结果集
        redisTemplate.boundHashOps("CART").delete(order.getUserId());

        //保存支付日志表
        PayLog payLog = new PayLog();
        //支付订单号
        long outTradeNo = idWorker.nextId();
        payLog.setOutTradeNo(String.valueOf(outTradeNo));
        //创建日期
        payLog.setCreateTime(new Date());
        //支付金额
        payLog.setTotalFee((long) (totalFee * 100));
        //用户ID
        payLog.setUserId(order.getUserId());
        //订单编号列表  list变成字符串为[11111,22222]
        payLog.setOrderList(ids.toString().replace("[","").replace("]",""));
        //支付类型
        payLog.setPayType("1");
        //交易状态
        payLog.setTradeState("0");

        //保存
        payLogDao.insertSelective(payLog);
        //保存到缓存中,方便快速取到payLog
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
    }

    //网页端分页查询订单及订单详情
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //$scope.searchMap={'pageNo':1,'pageSize':3,'userId':''};

        int pageNo = Integer.parseInt(searchMap.get("pageNo"));
        int pageSize = Integer.parseInt(searchMap.get("pageSize"));
        String userId = searchMap.get("userId");


        //resultMap 应该包含总页数 订单包装集合
        Map<String, Object> resultMap = new HashMap<>();
        //订单包装类集合
        List<OrderVo> orderVoList = new ArrayList<OrderVo>();

        //分页助手 ,对订单集合进行分页
        PageHelper.startPage(pageNo, pageSize);

        //查询当前用户的全部订单
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andUserIdEqualTo(userId);
        Page<Order> page = (Page<Order>) orderDao.selectByExample(orderQuery);
        List<Order> orderList = page.getResult();

        for (Order order : orderList) {
            //根据订单id查询订单详情集合
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
            List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);

            //封装数据
            OrderVo orderVo = new OrderVo();
            orderVo.setOrder(order);
            orderVo.setOrderItemList(orderItemList);

            orderVoList.add(orderVo);
        }
        //总页数
        resultMap.put("totalPages", page.getPages());
        //订单详情map
        resultMap.put("orderVoList", orderVoList);
        return resultMap;
    }

    //取消订单  订单状态为  6、交易关闭
    @Override
    public void cancleOrder(Long orderId) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus("6");
        orderDao.updateByPrimaryKeySelective(order);
    }
	
	 //分页查询每日新增订单
    @Override
    public PageResult searchManager(Integer page, Integer rows, Order order) {
        PageHelper.startPage(page, rows);
        OrderQuery orderQuery = new OrderQuery();

        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        criteria.andOrderIdEqualToOrderTime();

        if (null!=order.getOrderId()){
            criteria.andOrderIdEqualTo(order.getOrderId());
        }

        if ("1".equals(order.getStatus())){
            criteria.andStatusEqualTo(order.getStatus());
        }
        if ("2".equals(order.getStatus())){
            criteria.andStatusEqualTo(order.getStatus());
        }

        Page<Order> orders = (Page<Order>) orderDao.selectByExample(orderQuery);


        return  new PageResult(orders.getTotal(),orders.getResult());



    }
	
	@Override
    public List creatPic() {
        //获取商家id
        ArrayList list = new ArrayList<>();
        ArrayList list1 = new ArrayList<>();//时间集合
        ArrayList list2 = new ArrayList<>();//结果集合
            //获得每个商家的结果集[ [sellerid],[ [qiandu],[pinyougou],[yijia] ] ]
            List<entity.OrderVo> orderVos = orderDao.sumOrderPrice();
            for (entity.OrderVo orderVo : orderVos) {
                BigDecimal countprice = orderVo.getCountprice();
                String createday = orderVo.getCreateday();
                list1.add(createday);//[ [][][] ]
                list2.add(countprice);//[ [][][] ]
            }

        list.add(list1);
        list.add(list2);

        return list;

    }
}
