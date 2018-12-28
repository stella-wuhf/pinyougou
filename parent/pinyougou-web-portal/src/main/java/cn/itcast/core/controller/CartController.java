package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.Cart;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Reference
    private CartService cartService;

    /*添加购物车*/
    @RequestMapping("addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9003", allowCredentials = "true") //允许跨域访问
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Cart> cartList = null;
            //判断购物车集合的cookie是否存在
            boolean flag = false;
            //1. 获取cookie数组
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    //2. 获取Cookie中的购物车结果集
                    if ("CART".equals(cookie.getName())) {
                        cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                        flag = true;
                        break;
                    }
                }
            }
            //3. 判断cookie中是否有购物车,没有创建购物车结果集对象
            if (null == cartList) {
                cartList = new ArrayList<>();
            }
            //4. 在购物车中追加新商品  已有条件 Long itemId, Integer num
            //4.1 先准备新的购物车,不用考虑其他
            Cart newCart = new Cart();
            //4.2 通过库存ID查询库存对象
            Item item = cartService.findItemByItemId(itemId);
            //4.3 封装cart对象的商家id
            newCart.setSellerId(item.getSellerId());
            //4.4 准备商品对象orderItem
            OrderItem newOrderItem = new OrderItem();
            //4.5 封装orderItem对象的库存id和数量
            newOrderItem.setItemId(itemId);
            newOrderItem.setNum(num);
            //4.6 准备商品集合,并添加商品对象到集合中
            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            //4.7 封装cart对象的商品结果集
            newCart.setOrderItemList(newOrderItemList);
            //4.8 判断旧车集合中是否有新车
            int cartIndex = cartList.indexOf(newCart);
            //如果等于-1,则证明没有 ; 如果>=0 ,则是新车在旧车集合中的角标
            if (cartIndex != -1) {
                //旧车集合中有新车
                //获取到同新车是同一个商家的旧车,取出其中的商品结果集
                Cart oldCart = cartList.get(cartIndex);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                //4.9 判断旧商品集合中是否有新商品  判断新购物车中的新商品 在老购物车中商品结果集是否存在
                int orderItemIndex = oldOrderItemList.indexOf(newOrderItem);
                if (orderItemIndex != -1) {
                    //旧商品集合中有和新商品id一样的商品
                    OrderItem oldOrderItem = oldOrderItemList.get(orderItemIndex);
                    //找到对应的旧商品,修改其数量
                    oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                } else {
                    //旧商品集合中没有新商品,新商品添加
                    oldOrderItemList.add(newOrderItem);
                }
            } else {
                //旧车集合中没有新车,将新车添加到旧车结果集中
                cartList.add(newCart);
            }

            /* 前四步 将当前商品 和 cookie中可能有的购物车结果集 进行合并 作为一个新的购物车结果集 */

            //从springsecurity中获取用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //判断用户名是匿名(anonymousUser)还是真名
            if (!"anonymousUser".equals(name)) {
                //登录了,将合并后的购物车结果集 和 账户中可能有的购物车结果集 再次进行合并
                //5:将当前购物车结果集 合并到原来的购物车中
                cartService.merge(cartList, name);
                //6:清空Cookie
                if (flag) {//证明有购物车结合的cookie
                    Cookie cookie = new Cookie("CART", null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            } else {
                //没有登录
                //5. 将当前购物车 保存到Cookie
                Cookie cookie = new Cookie("CART", JSON.toJSONString(cartList));
                //设置路径和存活时间
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 5);
                //6. 回写到浏览器中
                response.addCookie(cookie);
            }

            return new Result(true, "添加购物车成功!");
        } catch (Exception e) {
            return new Result(false, "添加购物车失败!");
        }
    }

    /*跳转购物车页面后  查询购物车结果集*/
    @RequestMapping("findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {

        List<Cart> cartList = null;
        //1. 获取cookie数组
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //2. 获取Cookie中的购物车结果集
                if ("CART".equals(cookie.getName())) {
                    cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                    break;
                }
            }
        }
        //从springsecurity中获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断用户名是匿名(anonymousUser)还是真名
        if (!"anonymousUser".equals(name)) {
            //登录了
            //3:cookie中有 将购物车合并到帐户中原购物车 保存到缓存中 清空Cookie
            if (null != cartList) {
                cartService.merge(cartList, name);
                Cookie cookie = new Cookie("CART", null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            //4:将帐户中(缓存)购物车取出来
            cartList = cartService.findCartListFromRedis(name);
        }
        //5. 判断购物车集合是否为空,有就将结果集补全
        if (null != cartList) {
            cartList = cartService.findCatrList(cartList);
        }
        //6 回显
        return cartList;
    }
}