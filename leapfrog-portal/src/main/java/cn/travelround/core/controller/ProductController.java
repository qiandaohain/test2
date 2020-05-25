package cn.travelround.core.controller;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.Color;
import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.service.CmsService;
import cn.travelround.core.service.SearchService;
import cn.travelround.core.service.product.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Created by travelround on 2019/4/16.
 */
@Controller
public class ProductController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    // 搜索
    @Autowired
    private SearchService searchService;
    @Autowired
    private BrandService brandService;

    @RequestMapping(value = "/search")
    public String search(Integer pageNo, String keyword, Long brandId, String price, Model model) throws Exception {

        List<Brand> brands = brandService.selectBrandListFromRedis();
        model.addAttribute("brands", brands);

        // 传进来的数据再原样传回去,为了能够在判断是否显示品牌/价格栏时使用.
        model.addAttribute("brandId", brandId);
        model.addAttribute("price", price);

        // 已选条件 容器Map
        Map<String, String> map = new HashMap<>();
        // 品牌
        if (brandId != null) {
            for (Brand brand : brands) {
                if (brand.getId() == brandId) {
                    // key值也会显示在页面
                    map.put("品牌", brand.getName());
                    break;
                }
            }
        }
        // 价格 0-99 1600
        if (price != null) {
            if (price.contains("-")) {
                // 若价格样式为:0-99
                map.put("价格", price);
            } else {
                // 若价格样式为:1600以上
                map.put("价格", price + "以上");
            }
        }
        model.addAttribute("map", map);

        Pagination pagination = searchService.selectPaginationByQuery(pageNo, keyword, brandId, price);
        model.addAttribute("pagination", pagination);
        return "search";
    }

    @Autowired
    private CmsService cmsService;
    // 去商品详情页
    @RequestMapping(value = "/product/detail")
    public String detail(Long id, Model model) {
        // 商品
        Product product = cmsService.selectProductById(id);
        // 库存
        List<Sku> skus = cmsService.selectSkuListByProductId(id);

        // 相同颜色只保留一份
        Set<Color> colors = new HashSet<>();
        for (Sku sku : skus) {
            colors.add(sku.getColor());
        }

        model.addAttribute("product", product);
        model.addAttribute("skus", skus);
        model.addAttribute("colors", colors);
        return "product";
    }

}
