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
}
