package pers.wuyou.robot.listener;

import love.forte.simbot.api.message.events.GroupMsg;
import pers.wuyou.robot.core.annotation.DefaultValue;

/**
 * @author wuyou
 */
public class BootListener {
    public void boot(GroupMsg msg, String groupCode, String qqCode,@DefaultValue("boot_command") String bootCommand){
//        System.out.println(bootCommand);
        System.out.println("新增的打印");

    }
}
