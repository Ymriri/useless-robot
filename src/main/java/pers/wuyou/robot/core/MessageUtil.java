package pers.wuyou.robot.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.StringPool;
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
        return getDefaultValue(name, "");
    }

    public static String[] getDefaultValue(String name, String group) {
        if (name == null || name.isEmpty()) {
            return new String[0];
        }
        List<String> listenerValues = listenerValuesMapper.getDefaultValue(name, group);
        if (listenerValues == null || listenerValues.size() == 0) {
            QueryWrapper<DefaultValue> defaultValueQueryWrapper = new QueryWrapper<>();
            defaultValueQueryWrapper.eq(StringPool.NAME, name);
            DefaultValue defaultValue = defaultValueMapper.selectOne(defaultValueQueryWrapper);
            if (defaultValue == null) {
                return new String[0];
            }
            return new String[]{defaultValue.getValue()};
        }
        return listenerValues.toArray(new String[0]);
    }

}
