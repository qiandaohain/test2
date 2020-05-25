package cn.travelround.core.service.staticpage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Map;

/**
 * Created by travelround on 2019/4/18.
 */
public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {

    // 相当于:
    // Configuration configuration = new Configuration(Configuration.getVersion());
    // configuration.setDirectoryForTemplateLoading(new File("src/main/resources/"));
    private Configuration conf;
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.conf = freeMarkerConfigurer.getConfiguration();
    }

    // 静态化商品
    // root - 数据
    // id - 商品id
    public void productStaticPage(Map<String, Object> root, String id) {

        // 输出路径
        String path = getPath("/html/product/" + id + ".html");
        // 若父文件不存在就创建
        File f = new File(path);
        File parentFile = f.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        Writer out = null;
        try {
            // 加载模板文件
            Template template = conf.getTemplate("product.html");
            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
          template.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private ServletContext servletContext;
    private String getPath(String s) {
        return servletContext.getRealPath(s);
    }
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
