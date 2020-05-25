package cn.travelround.core.controller;

import cn.travelround.common.utils.RequestUtils;
import cn.travelround.core.bean.user.Buyer;
import cn.travelround.core.service.user.BuyerService;
import cn.travelround.core.service.user.SessionProvider;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by travelround on 2019/4/19.
 */
@Controller
public class LoginController {

    // 去登录页面
    @RequestMapping(value = "/login.aspx", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private SessionProvider sessionProvider; // 用来向redis存/取数据

    // 提交登录
    @RequestMapping(value = "/login.aspx", method = RequestMethod.POST)
    public String login(String username, String password, String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {

        if (username != null) {
            if (password != null) {
                // 账号和密码是否正确
                Buyer buyer = buyerService.selectBuyerByUsername(username);
                if (buyer != null) {
                    if (buyer.getPassword().equals(encodePassword(password))) {
                        // 保存用户信息 - 不要存在session中
                        // 正确方式:在redis中模拟session,将用户信息存到redis中
                        // 保存用户名到redis的session中
                        sessionProvider.setAttribuerForUsername(RequestUtils.getCSESSIONID(request, response), buyer.getUsername());
                        return "redirect:" + returnUrl;
                    } else {
                        model.addAttribute("error", "密码必须正确");
                    }
                } else {
                    model.addAttribute("error", "用户名必须正确");
                }

            } else {
                model.addAttribute("error", "密码不能为空");
            }
        } else {
            model.addAttribute("error", "用户名不能为空");
        }

        return "login";
    }

    private String encodePassword(String password) {

        // password = "jkygjg" + password + "uihjnj.adf";
        String algorithm = "MD5";
        char[] encodeHex = null;

        MessageDigest instance = null;
        try {
            instance = MessageDigest.getInstance(algorithm);
            byte[] digest = instance.digest(password.getBytes());// 完成加密
            // 十六进制加密
            // Hex - commons包下的
            encodeHex = Hex.encodeHex(digest);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String s = new String(encodeHex);

        System.out.println("加密后的密码:" + s);

        return s;
    }

    // 判断用户是否登录
    @RequestMapping(value = "/isLogin.aspx")
    public @ResponseBody MappingJacksonValue isLogin(String callback, HttpServletRequest request, HttpServletResponse response) {
        Integer result = 0;
        String username = sessionProvider.getAttribuerForUsername(RequestUtils.getCSESSIONID(request, response));
        if (username != null) {
            result = 1;
        }
        MappingJacksonValue mjv = new MappingJacksonValue(result);
        mjv.setJsonpFunction(callback);
        return mjv;
    }

}
