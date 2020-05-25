package cn.travelround;

import cn.travelround.core.bean.user.Buyer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.StringWriter;

/**
 * Created by travelround on 2019/4/20.
 */
public class TestJson {

    @Test
    public void testJSON() throws Exception {
        Buyer buyer = new Buyer();
        buyer.setUsername("范冰冰");

        ObjectMapper om = new ObjectMapper();
        // 设置值为null的字段不转换
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter w = new StringWriter();

        // 对象转Json
        // 一次性全转 - 不推荐
        // om.writeValueAsString(buyer);
        // 分部分的转 - 推荐
        om.writeValue(w, buyer);

        System.out.println(w.toString());

        // Json转对象
        Buyer r = om.readValue(w.toString(), Buyer.class);
        System.out.println(r);

    }

}
