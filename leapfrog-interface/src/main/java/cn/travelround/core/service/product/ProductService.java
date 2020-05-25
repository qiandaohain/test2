package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Color;
import cn.travelround.core.bean.product.Product;

import java.util.List;

/**
 * Created by travelround on 2019/4/12.
 */
public interface ProductService {
    Pagination selectPaginationByQuery(Integer pageNo, String name, Long brandId, Boolean isShow);

    List<Color> selectColorList();

    void insertProduct(Product product);

    void isShow(Long[] ids);
}
