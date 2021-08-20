package pers.wuyou.robot.listener;

import love.forte.simbot.api.sender.Setter;
import pers.wuyou.robot.core.annotation.DefaultValue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyou
 */
public class BanListener {
    public void banMember(Setter setter, String group, Set<String> set, @DefaultValue("time") Long time) {
        for (String s : set) {
            setter.setGroupBan(group, s, time, TimeUnit.MILLISECONDS);
        }
    }

}
