package pers.wuyou.robot.listener;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.utils.LoggerUtil;
import pers.wuyou.robot.utils.SenderUtil;

import java.util.Arrays;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class TestListener {
    public void test(GroupMsg msg, PrivateMsg pMsg, String group, String qq, @DefaultValue("test") String[] str, @DefaultValue("test2") String[] str2) {
        if (msg != null) {
            LoggerUtil.info(msg.getOriginalData());
///            SenderUtil.sendGroupMsg(msg, group, Arrays.toString(str2));
        }
        if (pMsg != null) {
            LoggerUtil.info(pMsg.getOriginalData());
            SenderUtil.sendPrivateMsg(pMsg, qq, Arrays.toString(str));
            SenderUtil.sendPrivateMsg(pMsg, qq, Arrays.toString(str2));
        }
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
