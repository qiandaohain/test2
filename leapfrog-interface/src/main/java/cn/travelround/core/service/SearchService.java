package cn.travelround.core.service;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.TestTb;
import cn.travelround.core.bean.product.Product;

import java.util.List;

/**
 * Created by travelround on 2019/4/9.
 */
public interface SearchService {

    Pagination selectPaginationByQuery(Integer pageNo, String keyword, Long brandId, String price) throws Exception;

    void insertProductToSolr(Long id);
}
