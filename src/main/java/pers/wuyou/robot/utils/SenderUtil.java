package pers.wuyou.robot.utils;

import catcode.Neko;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.config.MessageReplaceConfig;

import java.util.NoSuchElementException;

/**
 * 发送消息的工具类
 *
 * @author admin
 */
@Component
@SuppressWarnings("unused")
public class SenderUtil {
    public static Sender SENDER;

    public enum SendType {
        /**
         * 消息类型
         */
        GROUP, PRIVATE
    }

    @Autowired
    public SenderUtil(BotManager manager) {
        SenderUtil.SENDER = manager.getDefaultBot().getSender().SENDER;
    }

    /**
     * 发送群消息
     *
     * @param type    类型,group或private
     * @param code    群号或QQ号
     * @param message 消息内容
     */
    public static void sendMsg(MsgGet msgGet, SendType type, String code, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        if (msgGet != null) {
            message = MessageReplaceConfig.reconstructMessage(msgGet, message);
        }
        try {
            switch (type) {
                case GROUP:
                    SENDER.sendGroupMsg(code, message);
                    break;
                case PRIVATE:
                    SENDER.sendPrivateMsg(code, message);
                    break;
                default:
            }
        } catch (NoSuchElementException e) {
            SenderUtil.sendPrivateMsg(GlobalVariable.getADMINISTRATOR().get(0), "尝试给" + type + "[" + code + "]发送消息: " + message + " 失败");
        }

    }

    /**
     * 发送群消息,并解析
     *
     * @param msg     GroupMsg对象
     * @param group   群号
     * @param message 消息内容
     */
    public static synchronized void sendGroupMsg(GroupMsg msg, String group, String message) {
        sendMsg(msg, SendType.GROUP, group, message);
    }

    /**
     * 发送群消息
     *
     * @param group 群号
     * @param msg   消息内容
     */
    public static void sendGroupMsg(String group, MessageContent msg) {
        sendGroupMsg(group, msg.getMsg());
    }

    /**
     * 发送群消息
     *
     * @param group 群号
     * @param neko  猫猫码
     */
    public static void sendGroupMsg(String group, Neko neko) {
        sendGroupMsg(group, neko.toString());
    }

    /**
     * 发送群消息
     *
     * @param group   群号
     * @param message 消息内容
     */
    public static synchronized void sendGroupMsg(String group, String message) {
        sendMsg(null, SendType.GROUP, group, message);
    }

    /**
     * 发送私聊消息,并解析
     *
     * @param msg     PrivateMsg对象
     * @param qq      qq号
     * @param message 消息内容
     */
    public static synchronized void sendPrivateMsg(PrivateMsg msg, String qq, String message) {
        sendMsg(msg, SendType.PRIVATE, qq, message);
    }

    /**
     * 发送私聊消息
     *
     * @param qq      QQ号
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String qq, MessageContent message) {
        sendPrivateMsg(qq, message.getMsg());
    }

    /**
     * 发送私聊消息
     *
     * @param qq   QQ号
     * @param neko 猫猫码
     */
    public static void sendPrivateMsg(String qq, Neko neko) {
        sendPrivateMsg(qq, neko.toString());
    }

    /**
     * 发送私聊消息
     *
     * @param qq      QQ号
     * @param message 消息内容
     */
    public static void sendPrivateMsg(String qq, String message) {
        sendMsg(null, SendType.PRIVATE, qq, message);
    }

}
