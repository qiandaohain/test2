package cn.travelround.core.service;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.Sku;

import java.util.List;

/**
 * Created by travelround on 2019/4/9.
 */
public interface CmsService {


    Product selectProductById(Long id);

    List<Sku> selectSkuListByProductId(Long id);
}
