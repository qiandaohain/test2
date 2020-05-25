package cn.travelround.core.controller;

import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.service.product.SkuService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by travelround on 2019/4/15.
 */
@Controller
public class SkuController {

    @Autowired
    private SkuService skuService;

    @RequestMapping(value = "/sku/list.do")
    public String list(Long productId, Model model) {

        List<Sku> skus = skuService.selectSkuListByProductId(productId);
        System.out.println("skus:" + skus);
        model.addAttribute("skus", skus);

        return "sku/list";
    }

    @RequestMapping(value = "/sku/addSku.do")
    public void addSku(Sku sku, HttpServletResponse response) throws IOException {
        skuService.updateSkuById(sku);
        JSONObject jo = new JSONObject();
        jo.put("message", "保存成功!");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jo.toString());
    }

}
