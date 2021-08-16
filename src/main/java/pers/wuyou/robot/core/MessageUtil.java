package pers.wuyou.robot.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.forte.simbot.api.message.events.MsgGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.StringVariable;
import pers.wuyou.robot.config.MessageReplaceConfig;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.mapper.DefaultValueMapper;
import pers.wuyou.robot.mapper.ListenerValuesMapper;

import java.util.List;

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
        if (name == null || name.isEmpty()) {
            return new String[0];
        }
        List<String> listenerValues = listenerValuesMapper.getDefaultValue(name);
        if (listenerValues == null || listenerValues.size() == 0) {
            QueryWrapper<DefaultValue> defaultValueQueryWrapper = new QueryWrapper<>();
            defaultValueQueryWrapper.eq(StringVariable.NAME, name);
            DefaultValue defaultValue = defaultValueMapper.selectOne(defaultValueQueryWrapper);
            return new String[]{defaultValue.getValue()};
        }
        return listenerValues.toArray(new String[0]);
    }

    public static boolean verifyMessage(MsgGet msg, String arg0, String arg1, boolean trim) {
        String message = trim ? arg0.trim() : arg0,
                str = trim ? arg1.trim() : arg1;
        if (message.isEmpty() && str.isEmpty()) {
            return true;
        }
        str = MessageReplaceConfig.reconstructMessage(msg, str);
        return str.equals(message);
    }

}
