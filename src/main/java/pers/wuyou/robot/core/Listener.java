package pers.wuyou.robot.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import pers.wuyou.robot.common.Cat;

import java.lang.reflect.Method;

/**
 * 监听器
 *
 * @author wuyou
 */
@Data
@Builder
@AllArgsConstructor
public class Listener {
    private Integer id;
    /**
     * 实例化后的对象
      */

    private Object instance;
    /**
     * 对应的监听器方法
     */
    private Method method;
    /**
     * 监听器的别名,若不配置则为method.getName()
     */
    private String name;
    /**
     * 监听类型数组
     */
    private Class<? extends MsgGet>[] type;
    /**
     * 监听器优先级(1-10),优先级越大越先执行
     */
    private int priority;
    /**
     * 是否艾特了bot,如果消息内容里没有艾特bot则不执行
     */
    private boolean atBot;
    /**
     * 是否存在艾特,如果消息内容里没有艾特任何人则不执行
     */
    private boolean atAny;
    /**
     * 是否是开机状态,如果不是开机状态则不执行
     */
    private boolean isBoot;
    /**
     * 被阻断的监听器数组
     */
    private Integer[] breakListeners;

    @SuppressWarnings("RedundantIfStatement")
    public boolean validation(MsgGet msg) {
        if (atBot && !Cat.atBot(msg)) {
            return false;
        }
        if (atAny && msg instanceof GroupMsg && Cat.getAt(((GroupMsg) msg).getMsg()) == null) {
            return false;
        }
//
//        MessageContent msgContent;
//        if (msg instanceof GroupMsg) {
//            msgContent = ((GroupMsg) msg).getMsgContent();
//        } else if (msg instanceof PrivateMsg) {
//            msgContent = ((PrivateMsg) msg).getMsgContent();
//        } else {
//            return true;
//        }
//        if (keyword != null) {
//            if (trim) {
//                keyword = keyword.trim();
//            }
//            return keyword.isEmpty() || keywordMatchType.match(msgContent.getMsg(), keyword);
//        }
//        if (customFilters != null && !filterMatchType.mostMatch(customFilters, msg)) {
//            return false;
//        }
//        return true;

        return true;
    }

}
