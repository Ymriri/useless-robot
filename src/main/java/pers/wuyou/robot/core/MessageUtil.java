package pers.wuyou.robot.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.entity.ListenerValue;
import pers.wuyou.robot.mapper.DefaultValueMapper;
import pers.wuyou.robot.mapper.ListenerValueMapper;

import java.util.List;

/**
 * @author wuyou
 */
@Component
public class MessageUtil {
    private static ListenerValueMapper listenerValueMapper;
    private static DefaultValueMapper defaultValueMapper;

    @Autowired
    public MessageUtil(DefaultValueMapper defaultValueMapper, ListenerValueMapper listenerValueMapper) {
        MessageUtil.listenerValueMapper = listenerValueMapper;
        MessageUtil.defaultValueMapper = defaultValueMapper;
    }

    public static String[] getDefaultValue(int id, String name) {
        if (name == null || name.isEmpty()) {
            return new String[0];
        }
        QueryWrapper<ListenerValue> listenerValueQueryWrapper = new QueryWrapper<>();
        listenerValueQueryWrapper.select("value").eq("listener_id", id).eq("name", name);
        List<String> listenerValues = listenerValueMapper.getDefaultValue(id, name);
        if (listenerValues == null || listenerValues.size() == 0) {
            QueryWrapper<DefaultValue> defaultValueQueryWrapper = new QueryWrapper<>();
            defaultValueQueryWrapper.eq("name", name);
            DefaultValue defaultValue = defaultValueMapper.selectOne(defaultValueQueryWrapper);
            return new String[]{defaultValue.getValue()};
        }
        return listenerValues.toArray(new String[0]);
    }

    public static boolean verifyMessage(String arg0, String arg1, boolean trim) {
        String message = trim ? arg0.trim() : arg0,
                str = trim ? arg1.trim() : arg1;
        if (message.isEmpty() && str.isEmpty()) {
            return true;
        }
        str = replaceValue(str);
        return str.equals(message);
    }

    private static String replaceValue(String message) {
        String atBot = "\\$\\{@bot}";
        return message.replaceAll(atBot, Cat.atBot()).trim();
    }
}
