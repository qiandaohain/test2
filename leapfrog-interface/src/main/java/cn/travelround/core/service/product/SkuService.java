package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.BuyerCart;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.Sku;

import java.util.List;

/**
 * Created by travelround on 2019/4/10.
 */
public interface SkuService {

    List<Sku> selectSkuListByProductId(Long productId);

    void updateSkuById(Sku sku);

    void insertBuyerCartToRedis(BuyerCart buyerCart, String username);

    BuyerCart selectBuyerCartFromRedis(String username);

    Sku selectSkuById(Long id);
}
