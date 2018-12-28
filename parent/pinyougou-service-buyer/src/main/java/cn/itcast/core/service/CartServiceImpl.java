package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import pojogroup.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车管理
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;

    //通过库存ID查询库存对象
    @Override
    public Item findItemByItemId(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    //补全购物车结果集
    @Override
    public List<Cart> findCatrList(List<Cart> cartList) {
        Item item = null;
        for (Cart cart : cartList) {
            //商家id已经有了
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //库存id和数量已经有了
                //获取到库存对象
                item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                //goodsId
                orderItem.setGoodsId(item.getGoodsId());
                //title
                orderItem.setTitle(item.getTitle());
                //单价price
                orderItem.setPrice(item.getPrice());
                //小计totalFee
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //picPath
                orderItem.setPicPath(item.getImage());
            }
            //商家名称
            cart.setSellerName(item.getSeller());
        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    //将当前购物车结果集 合并到原来的购物车中
    @Override
    public void merge(List<Cart> newCartList, String name) {
        //1. 从缓存中获取到旧的购物车结果集
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
        //2. 合并新旧购物车结果集
        oldCartList = mergeNewAndOld(oldCartList, newCartList);
        //3. 将合并后的购物车结果集保存到缓存中
        redisTemplate.boundHashOps("CART").put(name, oldCartList);
    }

    //合并新旧购物车结果集
    private List<Cart> mergeNewAndOld(List<Cart> oldCartList, List<Cart> newCartList) {
        //判断新购物车结果集不为null,不为空 ,但是通过加入购物车进入的方法,新购物车结果集一定不为空
        if (null != newCartList && newCartList.size() > 0) {
            //判断旧购物车结果集
            if (null != oldCartList && oldCartList.size() > 0) {
                //新旧都不为空,进行合并
                //遍历新购物车结果集,获取到每一个新购物车
                for (Cart newCart : newCartList) {
                    int cartIndex = oldCartList.indexOf(newCart);
                    if (cartIndex != -1){
                        //证明旧购物车结果集中有和 新购物车商家 同一个商家的购物车
                        Cart oldCart = oldCartList.get(cartIndex);
                        //获取到旧购物车中的商品结果集
                        List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                        //获取到新购物车中的商品结果集
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {
                            int orderItemIndex = oldOrderItemList.indexOf(newOrderItem);
                            if (orderItemIndex != -1){
                                //证明旧商品结果集中,有和新商品一样的商品,那么就修改就商品的数量
                                OrderItem oldOrderItem = oldOrderItemList.get(orderItemIndex);
                                oldOrderItem.setNum(oldOrderItem.getNum()+newOrderItem.getNum());
                            }else {
                                //商品结果集中没有和新商品一样的商品,在旧商品结果集中添加新商品
                                oldOrderItemList.add(newOrderItem);
                            }
                        }

                    }else {
                        //旧购物车结果集中 和新购物车 没有同商家的,直接把新购物车添加到就购物车结果集中
                        oldCartList.add(newCart);
                    }
                }
            }else {
                //为空,就没有合并,返回新购物车结果集
                return newCartList;
            }
        }
        return oldCartList;
    }

    //将帐户中(缓存)购物车取出来
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }
}
