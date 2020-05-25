package cn.travelround.core.service.product;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.*;
import cn.travelround.core.dao.product.ColorDao;
import cn.travelround.core.dao.product.ProductDao;
import cn.travelround.core.dao.product.SkuDao;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by travelround on 2019/4/12.
 */
@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Pagination selectPaginationByQuery(Integer pageNo, String name, Long brandId, Boolean isShow) {

        ProductQuery productQuery = new ProductQuery();
        productQuery.setPageNo(Pagination.cpn(pageNo));

        // 排序
        productQuery.setOrderByClause("id desc");
        ProductQuery.Criteria createCriteria = productQuery.createCriteria();
        StringBuilder params = new StringBuilder();
        if (name != null) {
            createCriteria.andNameLike("%" + name + "%");
            params.append("name=").append(name);
        }
        if (brandId != null) {
            createCriteria.andBrandIdEqualTo(brandId);
            params.append("&brandId=").append(brandId);
        }
        if (isShow != null) {
            createCriteria.andIsShowEqualTo(isShow);
            params.append("&isShow=").append(isShow);
        } else {
            createCriteria.andIsShowEqualTo(false);
            params.append("&isShow=").append(false);
        }

        Pagination pagination = new Pagination(
                productQuery.getPageNo(),
                productQuery.getPageSize(),
                productDao.countByExample(productQuery),
                productDao.selectByExample(productQuery)
        );
        pagination.pageView("/product/list.do", params.toString());
        return pagination;
    }

    // 查询颜色结果集
    @Autowired
    private ColorDao colorDao;

    @Override
    public List<Color> selectColorList() {
        ColorQuery colorQuery = new ColorQuery();
        colorQuery.createCriteria().andParentIdNotEqualTo(0L);
        return colorDao.selectByExample(colorQuery);
    }

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private Jedis jedis;

    @Override
    public void insertProduct(Product product) {

        Long id = jedis.incr("pno");
        product.setId(id);

        // 插入商品基础表
        // 下架状态
        product.setIsShow(false);
        // 删除 - 不删除
        product.setIsDel(true);
        productDao.insertSelective(product);
        System.out.println("product的回填id:" + product.getId());

        // 插入库存表
        String[] colors = product.getColors().split(",");
        String[] sizes = product.getSizes().split(",");

        for (String color : colors) {
            for (String size : sizes) {
                Sku sku = new Sku();
                sku.setProductId(product.getId());// 商品id
                sku.setColorId(Long.parseLong(color));// 颜色
                sku.setSize(size);// 尺码
                sku.setMarketPrice(999f);// 市场价
                sku.setPrice(666f);// 售价
                sku.setDeliveFee(8f);// 运费
                sku.setStock(0);// 库存
                sku.setUpperLimit(200);// 购买限制
                sku.setCreateTime(new Date());// 添加时间
                skuDao.insertSelective(sku);
            }
        }


    }

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void isShow(Long[] ids) {
        Product product = new Product();
        product.setIsShow(true);
        for (Long id : ids) {
            product.setId(id);
            productDao.updateByPrimaryKeySelective(product);

            // activemq 消息发送
            // jmsTemplate.send(key, value)
            // 因为mq.xml中配置了<property name="defaultDestinationName" value="productId"/>
            // 所以不写参数1时,默认key叫productId
            // 若不想用productId做key, 可以自己再添加,如:
            // jmsTemplate.send("brandId", value)
            jmsTemplate.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

            // 静态化


        }
    }

}
