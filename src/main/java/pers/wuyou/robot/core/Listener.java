package pers.wuyou.robot.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.common.GlobalVariable;

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
     * 匹配前是否去除前后空格
     */
    private Boolean trim;

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
     * 是否是备用监听
     */
    private Boolean isSpare;

    /**
     * 当参数中的人被at时触发,多个用","隔开
     */
    private String at;

    /**
     * 匹配这段消息的账号列表,多个用","隔开
     */
    private String codes;

    /**
     * 匹配当前消息的群列表,多个用","隔开
     */
    private String groups;
    /**
     * 过滤器字段数组
     */
    private String[] filterName;
    /**
     * 被阻断的监听器数组
     */
    private Integer[] breakListeners;

    @SuppressWarnings("RedundantIfStatement")
    public boolean validation(MsgGet msg) {
        String message = null;
        if (msg instanceof GroupMsg) {
            message = ((GroupMsg) msg).getMsg();
            String groupCode = ((GroupMsg) msg).getGroupInfo().getGroupCode();
            if (isBoot) {
                if (GlobalVariable.BOOT_MAP.get(groupCode) == null) {
                    GlobalVariable.BOOT_MAP.put(groupCode, false);
                    return false;
                } else {
                    if (!GlobalVariable.BOOT_MAP.get(groupCode)) {
                        return false;
                    }
                }
            }
            if (atAny && Cat.getAt(message) == null) {
                return false;
            }
            if (atBot && !Cat.atBot(msg)) {
                return false;
            }
        }
        if (msg instanceof PrivateMsg) {
            message = ((PrivateMsg) msg).getMsg();
        }
        if (filterName.length != 0) {
            boolean filter = false;
            for (String str : filterName) {
                if (MessageUtil.verifyMessage(message, str, trim)) {
                    filter = true;
                }
            }
            if (!filter) {
                return false;
            }
        }
        return true;
    }

}
