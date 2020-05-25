package cn.travelround.core.controller;

import cn.travelround.common.fdfs.FastDFSUtils;
import cn.travelround.common.web.Constants;
import cn.travelround.core.service.product.UploadService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by travelround on 2019/4/11.
 */
@Controller
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @RequestMapping(value = "/upload/uploadPic.do")
    public void uploadPic(@RequestParam(required = false) MultipartFile pic, HttpServletResponse response) throws IOException {
//        System.out.println("图片的名字:" + pic.getOriginalFilename());

        String path = uploadService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());

        // 拼接完整的图片地址
        String url = Constants.IMG_URL + path;

        System.out.println("完整的图片路径:" + url);

        // 构建json数据,回传
        JSONObject jo = new JSONObject();
        jo.put("url", url);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jo.toString());
    }

    @RequestMapping(value = "/upload/uploadPics.do")
    @ResponseBody
    public List<String> uploadPics(@RequestParam(required = false) MultipartFile[] pics, HttpServletResponse response) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile pic : pics) {
            String path = uploadService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());
            String url = Constants.IMG_URL + path;
            urls.add(url);
        }
        return urls;
    }

    // 上传富文本图片
    @RequestMapping(value = "/upload/uploadFck.do")
    public void uploadFck(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MultipartRequest mr = (MultipartRequest) request;
        Map<String, MultipartFile> fileMap = mr.getFileMap();

        Set<Map.Entry<String, MultipartFile>> entrySet = fileMap.entrySet();
        for (Map.Entry<String, MultipartFile> entry : entrySet) {
            MultipartFile pic = entry.getValue();

            String path = uploadService.uploadPic(pic.getBytes(), pic.getOriginalFilename(), pic.getSize());
            String url = Constants.IMG_URL + path;

            JSONObject jo = new JSONObject();
            jo.put("error", 0);
            jo.put("url", url);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(jo.toString());
        }

    }

}
