package cn.travelround.core.controller;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.bean.product.Color;
import cn.travelround.core.bean.product.Product;
import cn.travelround.core.service.product.BrandService;
import cn.travelround.core.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by travelround on 2019/4/9.
 */
@Controller
public class ProductController {

     @Autowired
     private ProductService productService;
    @Autowired
    private BrandService brandService;

    @RequestMapping(value = "/product/list.do")
    public String list(Model model, Integer pageNo, String name, Long brandId, Boolean isShow) {

        List<Brand> brands = brandService.selectBrandListByQuery(1);
        model.addAttribute("brands", brands);

        // 分页
        Pagination pagination = productService.selectPaginationByQuery(pageNo, name, brandId, isShow);

        model.addAttribute("pagination", pagination);
        model.addAttribute("name", name);
        model.addAttribute("brandId", brandId);
        if (isShow != null) {
            model.addAttribute("isShow", isShow);
        } else {
            model.addAttribute("isShow", false);
        }
        return "product/list";
    }

    // 去商品添加页
    @RequestMapping(value = "/product/toAdd.do")
    public String toAdd(Model model) {

        // 查询
        // 品牌
        List<Brand> brands = brandService.selectBrandListByQuery(1);
        model.addAttribute("brands", brands);

        List<Color> colors = productService.selectColorList();
        model.addAttribute("colors", colors);

        return "product/add";
    }

    // 商品提交
    @RequestMapping(value = "/product/add.do")
    public String add(Product product) {

        productService.insertProduct(product);
        return "redirect:/product/list.do";
    }

    // 批量 上架
    @RequestMapping(value = "/product/isShow.do")
    public String isShow(Long[] ids) {
        productService.isShow(ids);
        return "forward:/product/list.do";
    }

}
