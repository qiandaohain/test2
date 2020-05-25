package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Brand;

import java.util.List;

/**
 * Created by travelround on 2019/4/10.
 */
public interface BrandService {
    Pagination selectPaginationByQuery(String name, Integer isDisplay, Integer pageNo);

    Brand selectBrandById(Long id);

    void updateBrandById(Brand brand);

    void deletes(Long[] ids);

    List<Brand> selectBrandListByQuery(Integer isDisplay);

    List<Brand> selectBrandListFromRedis();
}
