package pers.wuyou.robot;

import org.junit.jupiter.api.Test;
import pers.wuyou.robot.utils.HttpUtil;

public class MedalUtil {
    @Test
    public  void getMedal() {
        String response = HttpUtil.get("https://tiyu.baidu.com/tokyoly/home/tab/%E5%A5%96%E7%89%8C%E6%A6%9C/from/mobile").getResponse();
        String substring = response.substring(response.indexOf("<div class=\"rank-list\" "), response.indexOf("</a></div>")+10);
        System.out.println(substring);
    }

}

