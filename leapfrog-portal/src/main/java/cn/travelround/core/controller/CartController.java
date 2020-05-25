package cn.travelround.core.controller;

import cn.travelround.common.utils.RequestUtils;
import cn.travelround.common.web.Constants;
import cn.travelround.core.bean.BuyerCart;
import cn.travelround.core.bean.BuyerItem;
import cn.travelround.core.bean.order.Order;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.service.product.SkuService;
import cn.travelround.core.service.user.BuyerService;
import cn.travelround.core.service.user.SessionProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by travelround on 2019/4/20.
 */
@Controller
public class CartController {

    @Autowired
    private SessionProvider sessionProvider;
    @Autowired
    private SkuService skuService;

    // 填数据到购物车
    @RequestMapping(value = "/addCart")
    public String addCart(Long skuId, Integer amount, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        BuyerCart buyerCart = null;

        // 1.从reques中去cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // 2.判断cookie中是否有购物车
                if (cookie.getName().equals(Constants.BUYER_CART)) {
                    // 若有,取值并将Json字符串转回对象格式
                    buyerCart = om.readValue(cookie.getValue(), BuyerCart.class);
                    // 有可能Tomcat版本高,导致cookie添加不进去值,下句代替上一句
                    // buyerCart = om.readValue(URLDecoder.decode(cookie.getValue(), "utf-8"), BuyerCart.class);
                    break;
                }
            }
        }
        // 3.没有 创建购物车
        if (buyerCart == null) {
            buyerCart = new BuyerCart();
        }
        // 4.将商品信息填入购物车,只添加库存ID和购买数量
        Sku sku = new Sku();
        sku.setId(skuId);
        BuyerItem buyerItem = new BuyerItem();
        buyerItem.setSku(sku);
        buyerItem.setAmount(amount);
        buyerCart.addItem(buyerItem);

        // 5.判断用户是否登录
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        if (username != null) {
            // 若已登录,把商品添加到redis中
            skuService.insertBuyerCartToRedis(buyerCart, username);
            // 清除之前的cookie
            Cookie cookie = new Cookie(Constants.BUYER_CART, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            // 若未登录,把商品添加到cookie中(要先转为Json格式)
            StringWriter w = new StringWriter();
            om.writeValue(w, buyerCart);
            Cookie cookie = new Cookie(Constants.BUYER_CART, w.toString());
            // 有可能Tomcat版本高,导致cookie添加不进去值,下句代替上一句
            // Cookie cookie = new Cookie(Constants.BUYER_CART, URLEncoder.encode(w.toString(), "UTF-8"));
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        // 注意不要直接调用方法,而是重定向的方式,为了防止用户在购物车页面刷新导致数据重复添加
        return "redirect:/toCart";
    }

    // 去购物车页面
    @RequestMapping(value = "/toCart")
    public String toCart(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        BuyerCart buyerCart = null;

        // 1.从reques中去cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // 2.判断cookie中是否有购物车
                if (cookie.getName().equals(Constants.BUYER_CART)) {
                    // 若有,取值并将Json字符串转回对象格式
                    buyerCart = om.readValue(cookie.getValue(), BuyerCart.class);
                    // 有可能Tomcat版本高,导致cookie添加不进去值,下句代替上一句
                    // buyerCart = om.readValue(URLDecoder.decode(cookie.getValue(), "utf-8"), BuyerCart.class);
                    break;
                }
            }
        }

        // 3.判断用户是否登录
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        if (username != null) {
            // 若登录
            // 判断cookie中购物车中是否有商品
            if (buyerCart != null) {
                // 若登录前在cookie中的购物车添加了商品,将商品移到redis中并删除cookie
                skuService.insertBuyerCartToRedis(buyerCart, username);
                // 清除之前的cookie
                Cookie cookie = new Cookie(Constants.BUYER_CART, null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            // 从redis中查询购物车信息
            buyerCart = skuService.selectBuyerCartFromRedis(username);
        }
        // 判断redis中购物车中是否有商品
        if (buyerCart != null) {
            for (BuyerItem buyerItem : buyerCart.getItems()) {
                // redis购物车中只存了库存id和购买数量,去数据库搜索详细信息
                buyerItem.setSku(skuService.selectSkuById(buyerItem.getSku().getId()));
            }
        }
        model.addAttribute("buyerCart", buyerCart);
        return "cart";
    }

    // 结算
    @RequestMapping(value = "/buyer/trueBuy")
    public String trueBuy(Long[] skuIds, Model model, HttpServletRequest request, HttpServletResponse response) {

        // 1.判断购物车中是否有商品
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        BuyerCart buyerCart = skuService.selectBuyerCartFromRedis(username);
        List<BuyerItem> items = buyerCart.getItems();

        // 定义个标记, 若任一商品没货,就设置为true
        Boolean flag = false;
        if (items.size() > 0) {
            for (BuyerItem buyerItem : items) {
                buyerItem.setSku(skuService.selectSkuById(buyerItem.getSku().getId()));
                // 判断库存
                if (buyerItem.getAmount() > buyerItem.getSku().getStock()) {
                    // 无货
                    buyerItem.setIsHave(false);
                    flag = true;
                }
            }
            // 至少一款无货
            if (flag) {
                // 页面显示哪个无货
                model.addAttribute("buyerCart", buyerCart);
                // 返回购物车页面
                return "cart";
            }
        } else {
            return "redirect:/toCart";
        }
        // 若购物车中有商品,且库存充足,跳转到订单页面
        return "order";
    }

    // 提交订单
    @Autowired
    private BuyerService buyerService;

    @RequestMapping(value = "/buyer/submitOrder")
    public String submitOrder(Order order, Model model, HttpServletRequest request, HttpServletResponse response) {
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        buyerService.insertOrder(order, username);
        return "success";
    }
}
