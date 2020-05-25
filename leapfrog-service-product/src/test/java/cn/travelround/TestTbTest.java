package cn.travelround;

import cn.travelround.core.bean.TestTb;
import cn.travelround.core.dao.TestTbDao;
import cn.travelround.core.service.TestTbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by travelround on 2019/4/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class TestTbTest {

    @Autowired
    // private TestTbDao testTbDao;
    private TestTbService testTbService;

    @Test
    public void testAdd() throws Exception {
        TestTb testTb = new TestTb();
        testTb.setName("testName1");
        testTb.setBirthday(new Date());
        // testTbDao.insertTestTb(testTb);
        testTbService.insertTestTb(testTb);
    }

}
