package cn.travelround.core.service.user;

/**
 * Created by travelround on 2019/4/19.
 */
public interface SessionProvider {

    void setAttribuerForUsername(String name, String value);
    String getAttribuerForUsername(String name);

}
