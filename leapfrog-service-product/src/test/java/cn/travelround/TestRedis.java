package cn.travelround;

import com.alibaba.druid.filter.AutoLoad;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

/**
 * Created by travelround on 2019/4/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class TestRedis {

    @Autowired
    private Jedis jedis;

    @Test
    public void testSpringJedis() throws Exception {
        jedis.set("abc", "aaa");
    }



    @Test
    public void testRedis() throws Exception {
        Jedis jedis = new Jedis("172.16.52.214", 6379);
        Long pno = jedis.incr("pno");
        System.out.println(pno);
        jedis.close();
    }

}
