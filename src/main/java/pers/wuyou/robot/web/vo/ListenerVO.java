package pers.wuyou.robot.web.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import pers.wuyou.robot.core.Listener;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author wuyou
 */
@Data
@NoArgsConstructor
public class ListenerVO {
    private Integer id;
    /**
     * 对应的监听器方法
     */
    private String method;

    /**
     * 监听器的别名,若不配置则为method.getName()
     */
    private String name;

    /**
     * 监听器简介,在前端展示
     */
    private String introduction;

    /**
     * 监听类型数组
     */
    private String type;

    /**
     * 监听器优先级(1-10),优先级越大越先执行
     */
    private Integer priority;

    /**
     * 匹配前是否去除前后空格
     */
    private Boolean trim;

    /**
     * 是否艾特了bot,如果消息内容里没有艾特bot则不执行
     */
    private Boolean atBot;

    /**
     * 是否存在艾特,如果消息内容里没有艾特任何人则不执行
     */
    private Boolean atAny;

    /**
     * 是否是开机状态,如果不是开机状态则不执行
     */
    private Boolean boot;

    /**
     * 是否是备用监听
     */
    private Boolean isSpare;

    /**
     * 当参数中的人被at时触发
     */
    private String[] at;

    /**
     * 匹配这段消息的账号列表
     */
    private String[] codes;

    /**
     * 匹配当前消息的群列表
     */
    private String[] groups;
    /**
     * 过滤器字段数组
     */
    private String filterName;
    /**
     * 被阻断的监听器数组
     */
    private Integer[] breakListeners;
    /**
     * 更新时间,自动更新
     */
    private Date updateTime;
    /**
     * 类名
     */
    private String className;

    public static ListenerVO build(Listener listener) {
        ListenerVO that = new ListenerVO();
        that.setId(listener.getId());
        that.setName(listener.getName());
        that.setIntroduction(listener.getIntroduction());
        that.setPriority(listener.getPriority());
        that.setTrim(listener.getTrim());
        that.setAtBot(listener.isAtBot());
        that.setAtAny(listener.isAtAny());
        that.setBoot(listener.isBoot());
        that.setIsSpare(listener.getIsSpare());
        that.setAt(listener.getAt());
        that.setCodes(listener.getCodes());
        that.setGroups(listener.getGroups());
        that.setFilterName(listener.getFilterName());
        that.setBreakListeners(listener.getBreakListeners());
        that.setUpdateTime(listener.getUpdateTime());
        that.setMethod(listener.getMethod().getName());
        that.setClassName(listener.getInstance().getClass().getName());
        that.setType(Arrays.stream(listener.getType()).map(Class::getSimpleName).collect(Collectors.joining(",")));
        return that;
    }
}
