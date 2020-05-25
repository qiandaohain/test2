package cn.travelround.core.controller;

import cn.itcast.common.page.Pagination;
import cn.travelround.core.bean.product.Brand;
import cn.travelround.core.service.product.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by travelround on 2019/4/9.
 */
@Controller
public class BrandController {

    // Pagination 分页
    // 用法:
    // java代码中
    // 1. Pagination pagination = new Pagination(当前页,每页显示行数,所有页总的商品数);
    // 2. pagination.setList(当前页的商品集合)
    // 3. pagination.pageView(点击页码要跳转的链接地址);


    // jsp代码中
    // 1. ${pagination.list} - 遍历可获取当前页的所有商品
    // 2. ${pagination.pageView} - 直接呈现页码样式与点击链接

    @Autowired
    private BrandService brandService;

    @RequestMapping(value = "/brand/list.do")
    public String list(Model model, String name, Integer isDisplay, Integer pageNo) { // model选择springframework包的

        Pagination pagination = brandService.selectPaginationByQuery(name, isDisplay, pageNo);
        model.addAttribute("pagination", pagination);

        model.addAttribute("name", name);

        // 当用户不选择时,默认为可用品牌
        if (isDisplay != null) {
            model.addAttribute("isDisplay", isDisplay);
        } else {
            model.addAttribute("isDisplay", 1);
        }

        return "brand/list";
    }

    // 去修改页面
    @RequestMapping(value = "/brand/toEdit.do")
    public String toEdit(Model model, Long id) {

        Brand brand = brandService.selectBrandById(id);
        model.addAttribute("brand", brand);

        return "brand/edit";
    }

    // 修改
    @RequestMapping(value = "/brand/edit.do")
    public String edit(Brand brand) {
        brandService.updateBrandById(brand);
        return "redirect:/brand/list.do";
    }

    // 删除
    @RequestMapping(value = "/brand/deletes.do")
    public String deletes(Long[] ids) {
        brandService.deletes(ids);
        return "forward:/brand/list.do";
    }

}
