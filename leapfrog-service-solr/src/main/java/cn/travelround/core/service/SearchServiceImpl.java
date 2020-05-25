package cn.travelround.core.service;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.ProductQuery;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.bean.product.SkuQuery;
import cn.travelround.core.dao.product.ProductDao;
import cn.travelround.core.dao.product.SkuDao;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by travelround on 2019/4/16.
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrServer solrServer;

    @Override
    public Pagination selectPaginationByQuery(Integer pageNo, String keyword, Long brandId, String price) throws Exception {

        // 关键字高亮步骤:
        // 一.设置高亮
        // 设置高亮开关
        // 设置高亮字段
        // 设置高亮样式

        // 二.取高亮


        ProductQuery productQuery = new ProductQuery();
        productQuery.setPageNo(Pagination.cpn(pageNo));
        productQuery.setPageSize(12);
        StringBuilder params = new StringBuilder();

        List<Product> products = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery();
        // 指定搜索关键词
        solrQuery.set("q", "name_ik:" + keyword);

        params.append("keyword=").append(keyword);

        // 过滤条件
        // 品牌
        if (brandId != null) {
            solrQuery.addFilterQuery("brandId:" + brandId);
        }
        // 价格0-99 1600
        if (price != null) {
            String[] p = price.split("-");
            if (p.length == 2) {
                // 0-99
                solrQuery.addFilterQuery("price:[" + p[0] + " TO " + p[1] + "]");
            } else {
                // 1600以上
                solrQuery.addFilterQuery("price:[" + p[0] + " TO *]");
            }
        }


        // 一.设置高亮
        // 打开高亮开关
        solrQuery.setHighlight(true);
        // 设置高亮字段
        solrQuery.addHighlightField("name_ik");
        // 设置高亮样式<span style='color:red'>2016</span>
        solrQuery.setHighlightSimplePre("<span style='color:red'>");
        solrQuery.setHighlightSimplePost("</span>");

        // 排序 - 价格升序
        // solrQuery.addSort("price asc");// 不规范写法
        solrQuery.addSort("price", SolrQuery.ORDER.asc);// 规范写法

        // 设置查询量
        solrQuery.setStart(productQuery.getStartRow());
        solrQuery.setRows(productQuery.getPageSize());


        // 获取查询结果
        QueryResponse response = solrServer.query(solrQuery);

        // 二.取高亮
        // 最外层Map<442,Map> - 找到一款商品
        // 内层Map<name_ik, List> - 找到字段
        // List集合是因为可能有多个标题
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();


        // 结果集
        SolrDocumentList docs = response.getResults();
        // 查询到的条数
        long numFound = docs.getNumFound();

        for (SolrDocument doc : docs) {
            // 取出信息到自定义的数据结构中
            Product product = new Product();
            // 商品id
            String id = (String) doc.get("id");
            product.setId(Long.parseLong(id));

            // 商品名称
//            String name = (String) doc.get("name_ik");
//            product.setName(name);

            // 高亮显示
            Map<String, List<String>> map = highlighting.get(id);
            List<String> list = map.get("name_ik");
            product.setName(list.get(0));

            // 图片
            String url = (String) doc.get("url");
            product.setImgUrl(url);

            // 价格
            // Float price = (Float) doc.get("price");
            product.setPrice((Float) doc.get("price"));

            // 品牌id
            // Integer brandId = (Integer) doc.get("brandId");
            product.setBrandId(Long.parseLong(String.valueOf((Integer) doc.get("brandId"))));

            products.add(product);
        }
        // return products;
        Pagination pagination = new Pagination(
                productQuery.getPageNo(),
                productQuery.getPageSize(),
                (int)numFound,
                products
        );
        pagination.pageView("/search", params.toString());
        return pagination;
    }

    // 新建方法 - 将代码剪切到方法内
    @Autowired
    private ProductDao productDao;
    @Autowired
    private SkuDao skuDao;

    public void insertProductToSolr(Long id) {
        // 保存商品信息到Solr检索服务器
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", id); // 存商品id到solr

        // 存商品名到solr
        Product p = productDao.selectByPrimaryKey(id);
        doc.setField("name_ik", p.getName());

        // 存图片url到solr
        doc.setField("url", p.getImages()[0]);

        // 存售价到solr
        // select price from bbs_sku where product_id=442 order by price asc limit 0,1;
        SkuQuery skuQuery = new SkuQuery();
        skuQuery.createCriteria().andProductIdEqualTo(id);
        skuQuery.setOrderByClause("price asc"); // 按价格升序
        skuQuery.setPageNo(1);// 只查第一页
        skuQuery.setPageSize(1);// 每页显示一条数据
        skuQuery.setFields("price");// 指定查询字段
        List<Sku> skus = skuDao.selectByExample(skuQuery); // 查询
        doc.setField("price", skus.get(0).getPrice()); // 设置价格到solr

        // 存品牌id到solr
        doc.setField("brandId", p.getBrandId());

        try {
            solrServer.add(doc);
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
