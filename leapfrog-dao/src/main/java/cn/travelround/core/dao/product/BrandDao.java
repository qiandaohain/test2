package cn.travelround.core.dao.product;

import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.BrandQuery;

import java.util.List;

/**
 * Created by travelround on 2019/4/10.
 */
public interface BrandDao {
    Integer selectCount(BrandQuery brandQuery);

    List<Brand> selectBrandListByQuery(BrandQuery brandQuery);

    Brand selectBrandById(Long id);

    void updateBrandById(Brand brand);

    void deletes(Long[] ids);
}
