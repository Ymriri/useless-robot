package pers.wuyou.robot.utils;

import java.util.Random;

/**
 * 生成随机数
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class RandomUtil {
    final static Random RANDOM = new Random();

    /**
     * 生成随机数
     *
     * @return 1-10的随机数
     */
    public static int getRandom() {
        // 1-10
        return getRandom(10);
    }

    /**
     * 生成随机数
     *
     * @param bounds 最大值
     * @return 1-最大值的随机数
     */
    public static int getRandom(int bounds) {
        if (bounds == 0) {
            return 0;
        }
        return RANDOM.nextInt(bounds) + 1;
    }

    /**
     * 随机返回数组中的一条内容
     *
     * @param strings 字符串数组
     * @return 数组中的随机一条字符串
     */
    public static String getRandomString(String[] strings) {
        if (strings.length == 0) {
            return "";
        }
        if (strings.length == 1) {
            return strings[0];
        }
        return strings[RANDOM.nextInt(strings.length - 1) + 1];
    }
}
