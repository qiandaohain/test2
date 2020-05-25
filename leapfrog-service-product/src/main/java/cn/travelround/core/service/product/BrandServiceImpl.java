package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.BrandQuery;
import cn.travelround.core.dao.product.BrandDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by travelround on 2019/4/10.
 */
@Service("brandService")
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public Pagination selectPaginationByQuery(String name, Integer isDisplay, Integer pageNo) {

        BrandQuery brandQuery = new BrandQuery();
        brandQuery.setPageNo(Pagination.cpn(pageNo));
        brandQuery.setPageSize(3);

        StringBuilder params = new StringBuilder();
        if (name != null) {
            brandQuery.setName(name);
            params.append("name=").append(name);
        }
        if (isDisplay != null) {
            brandQuery.setIsDisplay(isDisplay);
            params.append("&isDisplay=").append(isDisplay);
        } else {
            brandQuery.setIsDisplay(1);
            params.append("&isDisplay=").append(1);
        }


        Pagination pagination = new Pagination(
                brandQuery.getPageNo(),
                brandQuery.getPageSize(),
                brandDao.selectCount(brandQuery));

        pagination.setList(brandDao.selectBrandListByQuery(brandQuery));

        pagination.pageView("/brand/list.do", params.toString());

        return pagination;
    }

    @Override
    public Brand selectBrandById(Long id) {
        return brandDao.selectBrandById(id);
    }

    @Autowired
    private Jedis jedis;

    @Override
    public void updateBrandById(Brand brand) {
        // 将品牌信息保存到redis,方便前台搜索页,筛选模块显示查询
        jedis.hset("brand", String.valueOf(brand.getId()), brand.getName());
        brandDao.updateBrandById(brand);
    }

    @Override
    public void deletes(Long[] ids) {
        brandDao.deletes(ids);
    }

    @Override
    public List<Brand> selectBrandListByQuery(Integer isDisplay) {

        BrandQuery brandQuery = new BrandQuery();
        brandQuery.setIsDisplay(isDisplay);
        return brandDao.selectBrandListByQuery(brandQuery);
    }

    // 从redis中查询品牌信息
    @Override
    public List<Brand> selectBrandListFromRedis() {

        List<Brand> brands = new ArrayList<>();
        // 从redis中查询
        Map<String, String> hgetAll = jedis.hgetAll("brand");

        // 变量查询结果,将数据存到自定义的结构中
        Set<Map.Entry<String, String>> entrySet = hgetAll.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            Brand brand = new Brand();
            brand.setId(Long.parseLong(entry.getKey()));
            brand.setName(entry.getValue());
            brands.add(brand);
        }
        return brands;
    }
}
