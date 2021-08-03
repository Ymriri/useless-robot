package pers.wuyou.robot.listener;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import pers.wuyou.robot.utils.LoggerUtil;

/**
 * @author wuyou
 */
public class TestListener {
    public void test(GroupMsg msg) {
        LoggerUtil.info(msg.getOriginalData());
        System.out.println(msg.getMsg());
    }

    public void test2(PrivateMsg msg) {
        LoggerUtil.info("执行了备用监听");
        LoggerUtil.info(msg.getOriginalData());
        System.out.println(msg.getMsg());
    }

    public void test3(PrivateMsg msg) {
        LoggerUtil.info("执行了阻断监听");
        LoggerUtil.info(msg.getOriginalData());
        System.out.println(msg.getMsg());
    }
}
