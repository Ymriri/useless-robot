package pers.wuyou.robot.core;

import catcode.Neko;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.entity.GroupListener;
import pers.wuyou.robot.mapper.GroupListenerMapper;
import pers.wuyou.robot.utils.TimeUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuyou
 */
@Component
public class ListenerUtil {
    private static GroupListenerMapper mapper;

    @Autowired
    public ListenerUtil(GroupListenerMapper mapper) {
        ListenerUtil.mapper = mapper;
    }

    public static boolean getListenerOpen(String group,int listenerId){
        QueryWrapper<GroupListener> wrapper = new QueryWrapper<>();
        wrapper.eq("group_code", group);
        wrapper.eq("listener_id", listenerId);
        GroupListener groupListener = mapper.selectOne(wrapper);
        return groupListener != null && groupListener.isOpen();
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
