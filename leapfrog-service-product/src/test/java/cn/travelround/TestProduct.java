package cn.travelround;

import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.ProductQuery;
import cn.travelround.core.dao.product.ProductDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by travelround on 2019/4/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class TestProduct {

    @Autowired
    private ProductDao productDao;

    @Test
    public void testAdd() throws Exception {
//        Product product = productDao.selectByPrimaryKey(441L);
//        System.out.println(product);

        // 条件,分页,指定字段 查询 排序
        ProductQuery productQuery = new ProductQuery();
        // and... 可拼接所有期望的筛选条件,个数不限
        productQuery.createCriteria().andBrandIdEqualTo(4L).andNameLike("%好莱坞%");
        // 以下拼接的条件是数据库表中没有对应字段
        // 分页
        productQuery.setPageNo(2);
        productQuery.setPageSize(10);
        // 排序
        productQuery.setOrderByClause("id desc");
        // 查询指定字段
        productQuery.setFields("id,brand_id");

        List<Product> products = productDao.selectByExample(productQuery);
        System.out.println(products);

        // 查询总条数
        int i = productDao.countByExample(productQuery);

        // 删除
        // productDao.deleteByPrimaryKey(441L);
        // productDao.deleteByExample(productQuery);

        // 修改
        // productDao.updateByPrimaryKey();
        // productDao.updateByPrimaryKeySelective();
        // productDao.updateByExample();
        // productDao.updateByExampleSelective();

        // 插入
        // productDao.insert();
        // productDao.insertSelective();


    }

}
