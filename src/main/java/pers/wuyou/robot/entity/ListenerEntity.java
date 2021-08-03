package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

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

    public String[] getType() {
        return type.split(",");
    }

    public Integer[] getBreakListeners() {
        String[] strings = this.breakListeners.split(",");
        Integer[] integers = new Integer[strings.length];
        for (int i = 0; i < strings.length; i++) {
            integers[i] = strings[i].isEmpty() ? -1 : Integer.parseInt(strings[i]);
        }
        return integers;
    }

    public String[] getStringArray(String key) {
        try {
            for (Field declaredField : this.getClass().getDeclaredFields()) {
                if(declaredField.getName().equals(underlineToHump(key))){
                    return declaredField.get(this).toString().split(",");
                }
            }
        } catch (Exception ignored) {
        }
        return new String[]{};
    }
    /***
     * 下划线命名转为驼峰命名.
     *
     * @param para 下划线命名的字符串
     */
    private String underlineToHump(String para) {
        if (StringUtils.isBlank(para)) {
            return para;
        }
        StringBuilder result = new StringBuilder();
        String[] a = para.split("_");
        if (a.length != 1) {
            for (String s : a) {
                if (result.length() == 0) {
                    result.append(s.toLowerCase());
                } else {
                    result.append(s.substring(0, 1).toUpperCase());
                    result.append(s.substring(1).toLowerCase());
                }
            }
        } else {
            result.append(a[0].substring(0, 1).toLowerCase());
            result.append(a[0].substring(1));
        }
        return result.toString();
    }
}
