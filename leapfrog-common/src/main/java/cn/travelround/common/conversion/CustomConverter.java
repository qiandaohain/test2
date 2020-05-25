package cn.travelround.common.conversion;


import org.springframework.core.convert.converter.Converter;

/**
 * Created by travelround on 2019/4/19.
 */
// 自定义转换器
// 去掉前后空格
// Converter - springframework包
public class CustomConverter implements Converter<String, String> {
    @Override
    public String convert(String source) {

        try {
            if (source != null) {
                source = source.trim();// 去掉首尾空格
                if (!"".equals(source)) {
                    return source;
                }
            }

        } catch (Exception e) {

        }
        return null;
    }
}
