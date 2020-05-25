package cn.travelround.core.service;

import cn.travelround.core.bean.TestTb;
import cn.travelround.core.dao.TestTbDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by travelround on 2019/4/9.
 */
@Service("testTbService") // 选择springframework包
@Transactional // 事务
public class TestTbServiceImpl implements TestTbService {

    @Autowired
    private TestTbDao testTbDao;

    @Override
    public void insertTestTb(TestTb testTb) {
        testTbDao.insertTestTb(testTb);
//        throw new RuntimeException();
    }
}
