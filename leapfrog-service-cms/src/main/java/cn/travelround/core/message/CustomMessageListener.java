package cn.travelround.core.message;

import cn.travelround.core.bean.product.Color;
import cn.travelround.core.bean.product.Product;
import cn.travelround.core.bean.product.Sku;
import cn.travelround.core.service.CmsService;
import cn.travelround.core.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.*;

/**
 * Created by travelround on 2019/4/18.
 */
public class CustomMessageListener implements MessageListener {

    @Autowired
    private StaticPageService staticPageService;
    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage am = (ActiveMQTextMessage) message;
        try {
            // System.out.println("cms模块接收到消息:" + am.getText());
            String id = am.getText();
            Map<String, Object> root = new HashMap<>();
            // 商品
            Product product = cmsService.selectProductById(Long.parseLong(id));
            // 库存
            List<Sku> skus = cmsService.selectSkuListByProductId(Long.parseLong(id));

            // 颜色去重
            Set<Color> colors = new HashSet<>();
            for (Sku sku : skus) {
                colors.add(sku.getColor());
            }
            root.put("product", product);
            root.put("skus", skus);
            root.put("colors", colors);

            // 静态化
            staticPageService.productStaticPage(root, id);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
