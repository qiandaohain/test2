package cn.travelround.core.service.user;

import cn.travelround.core.bean.BuyerCart;
import cn.travelround.core.bean.BuyerItem;
import cn.travelround.core.bean.order.Detail;
import cn.travelround.core.bean.order.Order;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.bean.user.Buyer;
import cn.travelround.core.bean.user.BuyerQuery;
import cn.travelround.core.dao.order.DetailDao;
import cn.travelround.core.dao.order.OrderDao;
import cn.travelround.core.dao.product.ColorDao;
import cn.travelround.core.dao.product.ProductDao;
import cn.travelround.core.dao.product.SkuDao;
import cn.travelround.core.dao.user.BuyerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Created by travelround on 2019/4/19.
 */
@Service("buyerService")
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private BuyerDao buyerDao;

    // 通过用户名查询对象
    @Override
    public Buyer selectBuyerByUsername(String username) {

        BuyerQuery buyerQuery = new BuyerQuery();
        buyerQuery.createCriteria().andUsernameEqualTo(username);

        List<Buyer> buyers = buyerDao.selectByExample(buyerQuery);

        if (buyers != null && buyers.size() > 0) {
            return buyers.get(0);
        }
        return null;
    }

    @Autowired
    private Jedis jedis;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private DetailDao detailDao;
    // 保存订单
    @Override
    public void insertOrder(Order order, String username) {

        Long id = jedis.incr("oid");
        order.setId(id);
        // 加载购物车
        BuyerCart buyerCart = selectBuyerCartFromRedis(username);
        List<BuyerItem> items = buyerCart.getItems();
        for (BuyerItem buyerItem : items) {
            buyerItem.setSku(selectSkuById(buyerItem.getSku().getId()));
        }
        // 运费
        order.setDeliverFee(buyerCart.getFee());
        // 总价
        order.setTotalPrice(buyerCart.getTotalPrice());
        // 订单金额
        order.setOrderPrice(buyerCart.getProductPrice());
        // 支付状态: 0 到付  1 待付款 2 已付款 3 待退款 4 退款成功 5 退款失败
        if (order.getPaymentWay() == 1) {
            order.setIsPaiy(0);
        } else {
            order.setIsPaiy(1);
        }

        // 订单状态: 0提交订单 1仓库配货 2商品出库 3等待收货 4完成 5待退货 6已退货
        order.setOrderState(0);
        // 时间
        order.setCreateDate(new Date());

        // 注册模块 注册成功会在redis中为当前账户生成一个全国唯一id
        String uid = jedis.get(username);
        order.setBuyerId(Long.parseLong(uid));

        // 保存订单
        orderDao.insertSelective(order);
        // 保存订单详情
        for (BuyerItem buyerItem : items) {
            Detail detail = new Detail();
            detail.setOrderId(id);
            detail.setProductId(buyerItem.getSku().getProductId());
            detail.setProductName(buyerItem.getSku().getProduct().getName());
            detail.setColor(buyerItem.getSku().getColor().getName());
            detail.setSize(buyerItem.getSku().getSize());
            detail.setPrice(buyerItem.getSku().getPrice());
            detail.setAmount(buyerItem.getAmount());
            detailDao.insertSelective(detail);
        }
        // 清空购物车
        jedis.del("buyerCart:" + username);
    }

    public BuyerCart selectBuyerCartFromRedis(String username) {
        BuyerCart buyerCart = new BuyerCart();
        Map<String, String> hgetAll = jedis.hgetAll("buyerCart:" + username);
        if (hgetAll != null) {
            Set<Map.Entry<String, String>> entrySet = hgetAll.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Sku sku = new Sku();
                sku.setId(Long.parseLong(entry.getKey()));
                BuyerItem buyerItem = new BuyerItem();
                buyerItem.setSku(sku);
                buyerItem.setAmount(Integer.parseInt(entry.getValue()));
                buyerCart.addItem(buyerItem);
            }
        }
        return buyerCart;
    }

    // 通过id查询sku对象
    @Autowired
    private ProductDao productDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private ColorDao colorDao;

    public Sku selectSkuById(Long id) {
        Sku sku = skuDao.selectByPrimaryKey(id);
        sku.setProduct(productDao.selectByPrimaryKey(sku.getProductId()));
        sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        return sku;
    }
}
