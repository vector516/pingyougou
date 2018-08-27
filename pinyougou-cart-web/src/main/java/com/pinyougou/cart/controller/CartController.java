package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pingyougou.entity.Result;
import com.pinyougou.cart.service.impl.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;


    /**
     * 购物车列表--->从cookie中取出购物车
     *
     * @param
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //得到登陆人账号,判断当前是否有人登陆--->未登录只要从cookie中读取,已登录还要从redis中读取
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }

        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        if (username.equals("anonymousUser")) {
            return cartList_cookie;
        } else {
            //从redis中读取数据
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);

            //并合并本地的cookie中的购物车
            if (cartList_cookie.size() > 0) {
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);

                //清除本地cookie的数据
                CookieUtil.deleteCookie(request, response, "cartList");

                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);

            }
            return cartList_redis;
        }

    }

    /**
     * 添加商品到购物车
     *
     * @param
     * @param
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
//    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num) {



        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户：" + username);
        try {
            List<Cart> cartList = findCartList();

            //调用服务将商品添加到购物车
            List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);

            if (username.equals("anonymousUser")) {
                //未登录,保存到cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(carts), 3600 * 24, "UTF-8");
                System.out.println("向cookie中存入数据");
            } else {
                //已登录,保存到redis中
                cartService.saveCartListToRedis(username, carts);
            }

            //解决ajax跨域访问的问题($http)
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return new Result(true, "添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }


}
