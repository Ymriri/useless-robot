package pers.wuyou.robot.web.utils;

import pers.wuyou.robot.entity.RequestEntity;
import pers.wuyou.robot.utils.HttpImageUtil;
import pers.wuyou.robot.utils.HttpUtil;
import org.apache.http.cookie.Cookie;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 */
public class QQLogin {
    private static final Map<String, Map<String, String>> COOKIES_MAP = new HashMap<>();

    /**
     * 初始化cookie
     *
     * @return cookie
     */
    public static Map<String, String> getAppCookies() {
        Map<String, String> cookiesMap = new HashMap<>();
        String url = "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?appid=11000101&target=self&style=40&s_url=https://pay.qq.com/ipay/login-proxy.html";
        RequestEntity requestEntity2 = HttpUtil.get(url);
        List<Cookie> cookies = requestEntity2.getCookies();
        new HashMap<>();

        for (Cookie cookie : cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        return cookiesMap;
    }

    /**
     * 获取二维码
     *
     * @param key 客户端的key
     * @return 二维码字节数组
     */
    public static byte[] getLoginQrCode(String key) {
        String now = new Date().getTime() + "";
        Map<String, String> cookiesMap = QQLogin.getAppCookies();
        String url1 = String.format("https://ssl.ptlogin2.qq.com/ptqrshow?appid=11000101&e=2&l=M&s=3&d=72&v=4&t=%s&pt_3rd_aid=0", now);
        RequestEntity imgRequestEntity = HttpImageUtil.get(url1, now + ".jpg");
        List<Cookie> cookies = imgRequestEntity.getCookies();
        for (Cookie cookie : cookies) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        QQLogin.COOKIES_MAP.put(key, cookiesMap);
        return (byte[]) imgRequestEntity.getOtherEntity();
    }

    /**
     * 获取登录状态
     *
     * @param key 客户端的key
     * @return 返回的登录实体
     */
    public static RequestEntity getLoginState(String key) {
        Map<String, String> map = QQLogin.COOKIES_MAP.get(key);
        String url_check_timeout =
                "https://ssl.ptlogin2.qq.com/ptqrlogin?u1=https://pay.qq.com/ipay/login-proxy.html&ptqrtoken=" + ptqrtoken_str(map.get("qrsig"))
                        + "&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=0-0-" + key
                        + "&js_ver=10233&js_type=1&login_sig=" + map.get("login_sig") + "&pt_uistyle=40&aid=11000101&";
        RequestEntity requestEntity = HttpUtil.get(url_check_timeout, null, map);
        System.out.println(requestEntity.getResponse());
        return requestEntity;
    }

//    /**
//     * 测试登录的方法
//     * @param group 群号
//     * @return
//     */
//    public static void login(String group) {
//
//        String now = new Date().getTime() + "";
//        Map<String, String> cookiesMap = getAppCookies();
//        String login_sig = cookiesMap.get("pt_login_sig");
//        String url1 = String.format("https://ssl.ptlogin2.qq.com/ptqrshow?appid=11000101&e=2&l=M&s=3&d=72&v=4&t=%s&pt_3rd_aid=0", now);
//        RequestEntity imgRequestEntity = HttpImageUtils.get(url1, now + ".jpg");
//        List<Cookie> cookies = imgRequestEntity.getCookies();
//        for (Cookie cookie : cookies) {
//            cookiesMap.put(cookie.getName(), cookie.getValue());
//        }
//        String imgPath = imgRequestEntity.getResponse();
//        SenderUtil.sendGroupMsg(group, Cat.getImage(imgPath));
//        String qrsig = cookiesMap.get("qrsig");
//        for (int i = 0; i < 20; i++) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            String url_check_timeout =
//                    "https://ssl.ptlogin2.qq.com/ptqrlogin?u1=https://pay.qq.com/ipay/login-proxy.html&ptqrtoken=" + ptqrtoken_str(qrsig)
//                            + "&ptredirect=0&h=1&t=1&g=1&from_ui=1&ptlang=2052&action=0-0-" + now
//                            + "&js_ver=10233&js_type=1&login_sig=" + login_sig + "&pt_uistyle=40&aid=11000101&";
//            RequestEntity requestEntity = HttpUtils.get(url_check_timeout, null, cookiesMap);
//            System.out.println(requestEntity.getResponse());
//            if (requestEntity.getResponse().contains("ptuiCB('0'")) {
//                break;
//            }
//        }
//    }

    /**
     * 计算qrsig
     *
     * @return 计算后的结果
     */
    private static int ptqrtoken_str(String qrsig) {
        int e = 0;
        int n = qrsig.length();
        for (int j = 0; j < n; j++) {
            e = e + (e << 5);
            e = e + ((int) (qrsig.toCharArray()[j]));
            e = 2147483647 & e;
        }
        return e;
    }

}
