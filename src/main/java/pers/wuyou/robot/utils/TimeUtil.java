package pers.wuyou.robot.utils;

import pers.wuyou.robot.core.MultiModeMatchUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author wuyou
 */
public class TimeUtil {

    /**
     * 根据时间文字获取时间长度(单位:毫秒)<br>
     * eg:<br>
     * str:     "三十秒"<br>
     * return:  300000L<br>
     *
     * @param str 时间文本
     * @return 时间长度
     */
    public static long getTime(String str) {
        MultiModeMatchUtil trie = new MultiModeMatchUtil("年", "月", "日", "天", "小时", "时", "分钟", "分", "秒");
        Map<String, String> map = trie.parseText(str);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        long start = calendar.getTimeInMillis();
        for (String keyword : map.keySet()) {
            int unit;
            try {
                unit = Integer.parseInt(map.get(keyword));
            } catch (Exception e) {
                unit = chineseNumber2Int(map.get(keyword));
            }
            switch (keyword) {
                case "年":
                    calendar.add(Calendar.YEAR, unit);
                    break;
                case "月":
                    calendar.add(Calendar.MONTH, unit);
                    break;
                case "日":
                case "天":
                    calendar.add(Calendar.DATE, unit);
                    break;
                case "小时":
                case "时":
                    calendar.add(Calendar.HOUR_OF_DAY, unit);
                    break;
                case "分钟":
                case "分":
                    calendar.add(Calendar.MINUTE, unit);
                    break;
                case "秒":
                    calendar.add(Calendar.SECOND, unit);
                    break;
                default:
            }
        }
        return calendar.getTimeInMillis() - start;
    }

    /**
     * 根据时间文字获取时间长度(单位:毫秒)<br>
     * eg:<br>
     * str:     "三十秒"<br>
     * return:  300000L<br>
     *
     * @param str 时间文本
     * @return 时间长度
     */
    public static Map<String, Object> getTimeWithExtra(String str) {
        MultiModeMatchUtil trie = new MultiModeMatchUtil("年", "月", "日", "天", "小时", "时", "分钟", "分", "秒");
        Map<String, String> map = trie.parseText(str);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        long start = calendar.getTimeInMillis();
        int index = 0;
        for (String keyword : map.keySet()) {
            String s = map.get(keyword);
            int unit;
            try {
                unit = Integer.parseInt(s);
            } catch (Exception e) {
                unit = chineseNumber2Int(s);
            }
            int l = str.indexOf(s + keyword) + (s + keyword).length();
            String sub = str.substring(str.indexOf(s) + s.length() - 1);
            boolean s0 = s.endsWith("半") || s.endsWith("半个");
            boolean s1 = str.contains(s + keyword + "半");
            switch (keyword) {
                case "年":
                    calendar.add(Calendar.YEAR, unit);
                    index = Math.max(l, index);
                    if (s0) {
                        calendar.add(Calendar.MONTH, 6);
                    }
                    if (s1) {
                        calendar.add(Calendar.MONTH, 6);
                        index++;
                    }
                    break;
                case "月":
                    calendar.add(Calendar.MONTH, unit);
                    index = Math.max(l, index);
                    if (s0) {
                        calendar.add(Calendar.DATE, 15);
                    }
                    if (s1) {
                        calendar.add(Calendar.DATE, 15);
                        index++;
                    }
                    break;
                case "日":
                case "天":
                    calendar.add(Calendar.DATE, unit);
                    index = Math.max(l, index);
                    if (s0) {
                        calendar.add(Calendar.HOUR_OF_DAY, 12);
                    }
                    if (s1) {
                        calendar.add(Calendar.HOUR_OF_DAY, 12);
                        index++;
                    }
                    break;
                case "小时":
                case "时":
                    calendar.add(Calendar.HOUR_OF_DAY, unit);
                    index = Math.max(l, index);
                    if (s0) {
                        calendar.add(Calendar.MINUTE, 30);
                    }
                    if (s1) {
                        calendar.add(Calendar.MINUTE, 30);
                        index++;
                    }
                    break;
                case "分钟":
                case "分":
                    calendar.add(Calendar.MINUTE, unit);
                    index = Math.max(l, index);
                    if (s0) {
                        calendar.add(Calendar.SECOND, 30);
                    }
                    if (s1) {
                        calendar.add(Calendar.SECOND, 30);
                        index++;
                    }
                    if (str.substring(index).startsWith("钟")) {
                        index++;
                    }
                    break;
                case "秒":
                    calendar.add(Calendar.SECOND, unit);
                    index = Math.max(l, index);
                    break;
                default:
            }
        }
        Map<String, Object> results = new HashMap<>(2);
        results.put("time", calendar.getTimeInMillis() - start);
        results.put("str", str.substring(index));
        return results;
    }

    private static int chineseNumber2Int(String chineseNumber) {
        int result = 0;
        int temp = 0;
        int count = 0;
        char[] cnArr = new char[]{'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {
                if (c == '两') {
                    temp = 2;
                    continue;
                }
                if (c == cnArr[j]) {
                    if (0 != count) {
                        result += temp;
                        count = 0;
                    }
                    temp = j % 10;
                    b = false;
                    break;
                }
            }
            if (b) {
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {
                result += temp;
            }
        }
        return result;
    }
}
