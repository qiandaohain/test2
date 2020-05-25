package cn.travelround.core.service.user;

import cn.travelround.common.web.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

/**
 * Created by travelround on 2019/4/19.
 */
public class SessionProviderImpl implements SessionProvider {

    @Autowired
    private Jedis jedis;
    private Integer exp = 30;// 单位分钟

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    @Override
    public void setAttribuerForUsername(String name, String value) {

        // 保存用户名到Redis中
        jedis.set(name + ":" + Constants.USER_NAME, value);
        // 设置存活时间
        jedis.expire(name + ":" + Constants.USER_NAME, 60 * exp);

    }

    @Override
    public String getAttribuerForUsername(String name) {
        String value = jedis.get(name + ":" + Constants.USER_NAME);
        if (value != null) {
            jedis.expire(name + ":" + Constants.USER_NAME, 60 * exp);
        }
        return value;
    }
}
