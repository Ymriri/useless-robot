package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.wuyou.robot.core.Listener;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * <p>
 * 监听器实体类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("listener")
public class ListenerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 监听器名称
     */
    private String name;

    /**
     * 监听器简介,在前端展示
     */
    private String introduction;

    /**
     * 监听类型,多个用","隔开
     */
    private String type;

    /**
     * 监听器类名
     */
    private String className;

    /**
     * 监听器方法名
     */
    private String methodName;

    /**
     * 过滤器字段名
     */
    private String filterName;

    /**
     * 是否需要开机
     */
    private Boolean isBoot = false;

    /**
     * 是否艾特任何人
     */
    private Boolean atAny = false;

    /**
     * 是否艾特bot
     */
    private Boolean atBot = false;

    /**
     * 匹配前是否去除前后空格
     */
    private Boolean trim = false;

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
     * 优先级, 默认为0
     */
    private Integer priority;

    /**
     * 是否是备用监听
     */
    private Boolean isSpare = false;

    /**
     * 被阻断的监听器数组,多个用","隔开
     */
    private String breakListeners;

    /**
     * 更新时间,自动更新
     */
    private Date updateTime;

    public String[] getType() {
        return type.split(",");
    }

    public Integer[] getBreakListeners() {
        String[] strings = breakListeners.split(",");
        Integer[] integers = new Integer[strings.length];
        for (int i = 0; i < strings.length; i++) {
            integers[i] = strings[i].isEmpty() ? -1 : Integer.parseInt(strings[i]);
        }
        return integers;
    }

    public Listener getListener(Object o, Method method) {
        return Listener.builder()
                .id(id)
                .instance(o)
                .method(method)
                .name(name)
                .priority(priority)
                .breakListeners(getBreakListeners())
                .trim(trim)
                .atBot(atBot)
                .atAny(atAny)
                .isBoot(isBoot)
                .at(at == null ? new String[0] : at.split(","))
                .codes(codes == null ? new String[0] : codes.split(","))
                .groups(groups == null ? new String[0] : groups.split(","))
                .isSpare(isSpare)
                .filterName(filterName)
                .updateTime(updateTime)
                .build();
    }

}
