package cn.travelround.core.interceptor;

import cn.travelround.common.utils.RequestUtils;
import cn.travelround.core.service.user.SessionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by travelround on 2019/4/20.
 */
public class CustomInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionProvider sessionProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 必须登录
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        if (username == null) {
            response.sendRedirect("http://localhost:8085/login.aspx?returnUrl=http://localhost:8082/");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
