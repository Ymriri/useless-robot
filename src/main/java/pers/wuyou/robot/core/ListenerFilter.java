package pers.wuyou.robot.core;

/**
 * @author wuyou
 */
public interface ListenerFilter {
    /**
     * 所有过滤器的实现类
     *
     * @param filterData 接收到的消息,包含GroupMsg和PrivateMsg
     * @return 过滤器结果
     */
    boolean test(FilterData filterData);
}
