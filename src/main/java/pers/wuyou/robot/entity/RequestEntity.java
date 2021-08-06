package pers.wuyou.robot.entity;

import lombok.Data;
import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * 响应实体类
 *
 * @author wuyou
 */
@Data
public class RequestEntity {
    List<Cookie> cookies;
    String response;
    Object otherEntity;

    @SuppressWarnings("unused")
    public String getCookie(String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
