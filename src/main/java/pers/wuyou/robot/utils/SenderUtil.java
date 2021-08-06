package pers.wuyou.robot.utils;

import catcode.Neko;
import pers.wuyou.robot.common.GlobalVariable;
import love.forte.simbot.api.message.MessageContent;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

import static pers.wuyou.robot.common.GlobalVariable.sender;

/**
 * 发送消息的工具类
 *
 * @author admin
 */
@Component
@SuppressWarnings("unused")
public class SenderUtil {

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
     * @param neko 猫猫码
     */
    public static void sendGroupMsg(String group, Neko neko) {
        sendGroupMsg(group, neko.toString());
    }

    /**
     * 发送群消息
     *
     * @param group 群号
     * @param msg   消息内容
     */
    public static synchronized void sendGroupMsg(String group, String msg) {
        sender.SENDER.sendGroupMsg(group, msg);
    }

    /**
     * 发送私聊消息
     *
     * @param qq  QQ号
     * @param msg 消息内容
     */
    public static void sendPrivateMsg(String qq, String msg) {
        sendPrivateMsg(qq, null, msg);
    }

    /**
     * 发送私聊消息
     *
     * @param qq  QQ号
     * @param neko 猫猫码
     */
    public static void sendPrivateMsg(String qq, Neko neko) {
        sendPrivateMsg(qq, neko.toString());
    }
    /**
     * 发送私聊消息
     *
     * @param qq  QQ号
     * @param msg 消息内容
     */
    public static void sendPrivateMsg(String qq, MessageContent msg) {
        sendPrivateMsg(qq, null, msg);
    }

    /**
     * 发送私聊消息
     *
     * @param qq    QQ号
     * @param group 发送消息的群号
     * @param msg   消息内容
     */
    public static void sendPrivateMsg(String qq, String group, MessageContent msg) {
        try {
                sender.SENDER.sendPrivateMsg(qq, group, msg);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            SenderUtil.sendPrivateMsg(GlobalVariable.ADMINISTRATOR.get(0), "尝试给[" + qq + "]发送消息: " + msg + " 失败");
        }
    }

    /**
     * 发送私聊消息
     *
     * @param qq    QQ号
     * @param group 发送消息的群号
     * @param msg   消息内容
     */
    public static synchronized void sendPrivateMsg(String qq, String group, String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        try {
                sender.SENDER.sendPrivateMsg(qq, group, msg);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            SenderUtil.sendPrivateMsg(GlobalVariable.ADMINISTRATOR.get(0), "尝试给[" + qq + "]发送消息: " + msg + " 失败");
        }
    }
}
