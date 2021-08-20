package pers.wuyou.robot.core;

import catcode.Neko;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.common.StringVariable;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.mapper.DefaultValueMapper;
import pers.wuyou.robot.mapper.ListenerValuesMapper;
import pers.wuyou.robot.utils.TimeUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuyou
 */
@Component
public class MessageUtil {
    private static ListenerValuesMapper listenerValuesMapper;
    private static DefaultValueMapper defaultValueMapper;

    @Autowired
    public MessageUtil(DefaultValueMapper defaultValueMapper, ListenerValuesMapper listenerValuesMapper) {
        MessageUtil.listenerValuesMapper = listenerValuesMapper;
        MessageUtil.defaultValueMapper = defaultValueMapper;
    }

    public static String[] getDefaultValue(String name) {
        return getDefaultValue(name, "");
    }

    public static String[] getDefaultValue(String name, String group) {
        if (name == null || name.isEmpty()) {
            return new String[0];
        }
        List<String> listenerValues = listenerValuesMapper.getDefaultValue(name, group);
        if (listenerValues == null || listenerValues.size() == 0) {
            QueryWrapper<DefaultValue> defaultValueQueryWrapper = new QueryWrapper<>();
            defaultValueQueryWrapper.eq(StringVariable.NAME, name);
            DefaultValue defaultValue = defaultValueMapper.selectOne(defaultValueQueryWrapper);
            return new String[]{defaultValue.getValue()};
        }
        return listenerValues.toArray(new String[0]);
    }

    public static boolean verifyMessage(Map<String, Object> args, String arg0, String arg1, boolean trim) {
        String message = trim ? arg0.trim() : arg0,
                str = trim ? arg1.trim() : arg1;
        if (message.isEmpty() && str.isEmpty()) {
            return true;
        }
        long time = 0;
        String reg = "\\$\\{.+?}|.";
        Pattern patten = Pattern.compile(reg);
        Matcher matcher = patten.matcher(str);
        while (matcher.find()) {
            String s = matcher.group();
            if ("${at}".equals(s)) {
                for (Neko at : Cat.getAtKqs(message)) {
                    if (message.startsWith(at.toString())) {
                        message = message.replace(at, "").trim();
                    }
                }
                continue;
            }
            if ("${time}".equals(s)) {
                try {
                    time = Integer.parseInt(message) * 60L;
                } catch (Exception e) {
                    time = TimeUtil.getTime(message);
                }
                if (time != 0) {
                    message = message.replace(message, "");
                    continue;
                }
            }
            if (message.startsWith(s)) {
                message = message.replace(s, "");
            } else {
                return false;
            }
        }
        if (message.isEmpty() && time > 0) {
            args.put("time", time);
        }
        return message.isEmpty();
    }

}
