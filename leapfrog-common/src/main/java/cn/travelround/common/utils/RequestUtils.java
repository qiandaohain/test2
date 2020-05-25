package cn.travelround.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by travelround on 2019/4/19.
 */
public class RequestUtils {

    // 获取sessionId
    public static String getCSESSIONID(HttpServletRequest request, HttpServletResponse response) {

        // 1.取出cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                // 2.判断cookie中是否有sessionid
                if (cookie.getName().equals("CSESSIONID")) {
                    // 3.若有, 直接使用
                    return cookie.getValue();
                }
            }
        }
        // 4.没有 创建一个sessionid, 并保存到cookie中
        String csessionid = UUID.randomUUID().toString().replaceAll("-", "");
        Cookie cookie = new Cookie("CSESSIONID", csessionid);
        // 大于0 存活时间
        // 等于0 立即删除
        // 小于0 关闭浏览器删除
        cookie.setMaxAge(-1);

        cookie.setPath("/");

        response.addCookie(cookie);
        return csessionid;
    }
}
