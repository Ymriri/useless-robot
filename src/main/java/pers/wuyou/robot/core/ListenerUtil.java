package pers.wuyou.robot.core;

import catcode.Neko;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.common.StringPool;
import pers.wuyou.robot.entity.BootStateInfo;
import pers.wuyou.robot.entity.GroupListener;
import pers.wuyou.robot.mapper.BootStateInfoMapper;
import pers.wuyou.robot.mapper.GroupListenerMapper;
import pers.wuyou.robot.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuyou
 */
@Component
public class ListenerUtil {
    private static GroupListenerMapper mapper;
    private static BootStateInfoMapper bootMapper;

    @Autowired
    public ListenerUtil(GroupListenerMapper mapper, BootStateInfoMapper bootMapper) {
        ListenerUtil.mapper = mapper;
        ListenerUtil.bootMapper = bootMapper;
    }

    public static boolean getListenerOpen(String group, int listenerId) {
        QueryWrapper<GroupListener> wrapper = new QueryWrapper<>();
        wrapper.eq("group_code", group);
        wrapper.eq("listener_id", listenerId);
        GroupListener groupListener = mapper.selectOne(wrapper);
        return groupListener != null && groupListener.isOpen();
    }

    public static boolean getGroupOpen(String group) {
        QueryWrapper<BootStateInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("group_code", group);
        BootStateInfo info = bootMapper.selectOne(wrapper);
        return info != null && info.getState();
    }

    /**
     * 验证消息内容,并返回其中的参数对应内容
     *
     * @param args    携带参数, 消息中的${time}, ${0}, ${1}...将会添加到这里
     * @param message 消息内容
     * @param verify  验证字符串
     * @param trim    验证时是否去除空格
     * @return 是否通过
     */
    @SuppressWarnings("AlibabaMethodTooLong")
    public static boolean verifyMessage(Map<String, Object> args, String message, String verify, boolean trim) {
        message = trim ? message.trim() : message;
        verify = trim ? verify.trim() : verify;
        if (message.isEmpty() && verify.isEmpty()) {
            return true;
        }
        String reg = "(\\$\\{.+?}){1,10}";
        long time;
        if (message.split(reg).length > 1) {
            // 如果消息内容中包含${x}就返回
            return false;
        }
        String[] split = verify.split(reg);
        for (String s : split) {
            if (!message.contains(trim ? s.trim() : s)) {
                // 消息中如果不包含除了${x}以外的字符串,则肯定不会匹配成功
                return false;
            }
        }
        Pattern patten = Pattern.compile(reg);
        Matcher matcher = patten.matcher(verify);
        List<String> argList = new ArrayList<>();
        while (matcher.find()) {
            // 添加所有的${x}字符串
            argList.add(matcher.group());
        }
        if (argList.size() == 0) {
            // 没有${x},直接判断是否相等
            return trim ? message.trim().equals(verify.trim()) : message.equals(verify);
        }
        for (Neko neko : Cat.getKq(message, StringPool.AT)) {
            // 将消息中所有艾特类型的猫猫码转成${@one}或${@bot},避免匹配时被猫猫码转成字符串的结果干扰
            if (GlobalVariable.getDefaultBotCode().equals(neko.get("code"))) {
                message = message.replace(neko.toString(), StringPool.LISTENER_FILTER_AT_BOT);
            } else {
                message = message.replace(neko.toString(), StringPool.LISTENER_FILTER_AT_ONE);
            }
        }
        // 用于储存${x}与消息内容的对应关系
        Map<String, String> regString = new HashMap<>(8);
        // 根据拆分结果多摸匹配
        MultiModeMatchUtil util = new MultiModeMatchUtil(split);
        Map<String, String> map = util.parseText(message);
        // 将多模匹配结果转为列表
        List<String> list = new ArrayList<>(map.values());
        // 记录统计的长度
        int len = 0;
        // 遍历次数
        int num = 0;
        for (int i = 0; i < split.length; i++) {
            // 遍历所有除去${x}的字符串
            String value = trim ? split[i].trim() : split[i];
            // 统计拆分字符串的长度
            len += value.length();
            // 如果拆分的字符串数组的第一项是"",则下标向后偏移一位
            int index = split.length - i - (split[0].isEmpty() ? 2 : 1);
            if (list.size() == 0) {
                continue;
            }
            if (index != -1) {
                regString.put(argList.get(num++), list.get(index));
                // 统计${x}对应字符的长度
                len += list.get(index).length();
            }
        }
        if (len <= message.length() && verify.endsWith(argList.get(argList.size() - 1))) {
            // 如果长度不一致,并且字符串是结尾是最后一个${x},则设置最后一个${x}的对应值为两个字符串相差的字符串
            regString.put(argList.get(argList.size() - 1), message.substring(len));
        }
        if (regString.size() != argList.size()) {
            return false;
        }
        for (String k : regString.keySet()) {
            // 遍历对应关系, 用于处理${x}${x}的情况
            String v = regString.get(k);
            String numReg = "\\$\\{\\d+}";
            if (!k.matches(numReg)) {
                // 如果${x}中的x不是纯数字,则拆分
                switch (k) {
                    case StringPool.LISTENER_FILTER_AT_ANY:
                        // 艾特多个
                        for (Neko at : Cat.getAtKqs(v)) {
                            if (!v.equals(at.toString())) {
                                return false;
                            }
                        }
                        break;
                    case StringPool.LISTENER_FILTER_AT_ONE:
                        // 艾特一个
                        if (Cat.getAtKqs(v).size() > 1) {
                            return false;
                        }
                        for (Neko at : Cat.getAtKqs(v)) {
                            if (!v.trim().equals(at.toString())) {
                                return false;
                            }
                        }
                        break;
                    case StringPool.LISTENER_FILTER_AT_BOT:
                        // 艾特bot
                        if (!k.equals(v.trim())) {
                            return false;
                        }
                        break;
                    case StringPool.LISTENER_FILTER_TIME:
                        // 时间
                        try {
                            time = Integer.parseInt(v) * 60L;
                        } catch (Exception e) {
                            time = TimeUtil.getTime(v);
                        }
                        if (time == 0) {
                            return false;
                        }
                        break;
                    default:
                        // 以上都不是,则为${x}${x}或者其他字符串的情况
                        String reg1 = "\\$\\{.+?}";
                        Pattern patten1 = Pattern.compile(reg1);
                        Matcher matcher1 = patten1.matcher(k);
                        if (!matcher1.find()) {
                            // 没有匹配到
                            return false;
                        }
                        do {
                            // 由于之前执行了find()方法,所以这里使用do-while
                            String str = matcher1.group();
                            if (StringPool.LISTENER_FILTER_AT_ANY.equals(str)) {
                                // 匹配通过的情况下,一定是以${one}开头
                                if (!v.startsWith(StringPool.LISTENER_FILTER_AT_ONE)) {
                                    return false;
                                }
                                while (v.startsWith(StringPool.LISTENER_FILTER_AT_ONE)) {
                                    v = v.replace(StringPool.LISTENER_FILTER_AT_ONE, "").trim();
                                }
                            } else if (StringPool.LISTENER_FILTER_AT_ONE.equals(str)) {
                                if (!v.startsWith(str) && !v.startsWith(StringPool.LISTENER_FILTER_AT_BOT)) {
                                    return false;
                                }
                                v = v.replace(StringPool.LISTENER_FILTER_AT_ONE, "").replace(StringPool.LISTENER_FILTER_AT_BOT, "").trim();
                            } else if (StringPool.LISTENER_FILTER_AT_BOT.equals(str)) {
                                if (!v.startsWith(StringPool.LISTENER_FILTER_AT_BOT)) {
                                    return false;
                                }
                                v = v.replace(StringPool.LISTENER_FILTER_AT_BOT, "").trim();
                            } else if (StringPool.LISTENER_FILTER_TIME.equals(str)) {
                                // 如果是时间的话,先抽取出其中的时间字符串,然后返回其他部分的内容
                                try {
                                    time = Integer.parseInt(v) * 60 * 1000L;
                                    v = "";
                                } catch (Exception e) {
                                    Map<String, Object> res = TimeUtil.getTimeWithExtra(v);
                                    time = (long) res.get("time");
                                    v = ((String) res.get("str")).trim();
                                }
                                if (time == 0) {
                                    return false;
                                } else {
                                    args.put("time", time);
                                }
                            } else {
                                // 有其他${x}的字符串,匹配不通过
                                return false;
                            }
                        } while (matcher1.find());
                        if (!v.isEmpty()) {
                            // 字符串除去被替换的部分还有其他内容,匹配失败
                            return false;
                        }
                        break;
                }
            } else {
                // 如果${x}中的x是纯数字,则添加到参数列表中
                args.put(k, v);
            }
        }
        return true;
    }

}
