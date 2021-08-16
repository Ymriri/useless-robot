package pers.wuyou.robot.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.entity.ListenerValues;

import java.util.List;

/**
 * @author wuyou
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultValueVO extends DefaultValue {
    /**
     * 动态值的数量
     */
    private Integer valueCount;
    /**
     * 动态值数组
     */
    private List<ListenerValues> values;
}
