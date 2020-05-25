package cn.travelround.core.service;

import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.bean.product.SkuQuery;
import cn.travelround.core.dao.product.ColorDao;
import cn.travelround.core.dao.product.ProductDao;
import cn.travelround.core.dao.product.SkuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by travelround on 2019/4/18.
 */
@Service("cmsService")
public class CmsServiceImpl implements CmsService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private ColorDao colorDao;

    @Override
    public Product selectProductById(Long id) {
        return productDao.selectByPrimaryKey(id);
    }

    // 查询库存
    // 对于无货商品的处理:
    // 淘宝 - 无货商品置灰
    // 京东 - 只显示有货商品
    @Override
    public List<Sku> selectSkuListByProductId(Long id) {

        SkuQuery skuQuery = new SkuQuery();
        skuQuery.createCriteria().andProductIdEqualTo(id).andStockGreaterThan(0);// 库存大于0
        List<Sku> skus = skuDao.selectByExample(skuQuery);
        for (Sku sku : skus) {
            sku.setColor(colorDao.selectByPrimaryKey(sku.getColorId()));
        }
        return skus;
    }
}
