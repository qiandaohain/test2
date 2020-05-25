package cn.travelround.core.service.product;

import cn.travelround.core.bean.BuyerCart;
import cn.travelround.core.bean.BuyerItem;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.bean.product.SkuQuery;
import cn.travelround.core.dao.product.ColorDao;
import cn.travelround.core.dao.product.ProductDao;
import cn.travelround.core.dao.product.SkuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by travelround on 2019/4/15.
 */
@Service("skuService")
@Transactional
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private ColorDao colorDao;

    @Override
    public List<Sku> selectSkuListByProductId(Long productId) {

        SkuQuery skuQuery = new SkuQuery();
        skuQuery.createCriteria().andProductIdEqualTo(productId);
        List<Sku> skus = skuDao.selectByExample(skuQuery);

        for (Sku sku : skus) {
            sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        }
        return skus;
    }

    @Override
    public void updateSkuById(Sku sku) {
        skuDao.updateByPrimaryKeySelective(sku);
    }



    @Autowired
    private Jedis jedis;
    @Override
    public void insertBuyerCartToRedis(BuyerCart buyerCart, String username) {

        // 判断购物车商品数量大于0
        List<BuyerItem> items = buyerCart.getItems();
        if (items.size() > 0) {
            for (BuyerItem buyerItem : items) {
                // 判断商品是否已经存在
                if (jedis.hexists("buyerCart:" + username, String.valueOf(buyerItem.getSku().getId()))) {
                    // 若已经存在
                    jedis.hincrBy("buyerCart:" + username, String.valueOf(buyerItem.getSku().getId()), buyerItem.getAmount());
                } else {
                    // 若不存在
                    jedis.hset("buyerCart:" + username, String.valueOf(buyerItem.getSku().getId()), String.valueOf(buyerItem.getAmount()));
                }

            }
        }
    }

    @Override
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
    @Override
    public Sku selectSkuById(Long id) {
        Sku sku = skuDao.selectByPrimaryKey(id);
        sku.setProduct(productDao.selectByPrimaryKey(sku.getProductId()));
        sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        return sku;
    }





}
