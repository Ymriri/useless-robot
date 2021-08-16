package pers.wuyou.robot.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.wuyou.robot.entity.ListenerValues;

/**
 * @author wuyou
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListenerValuesVO extends ListenerValues {

    /**
     * 字段名
     */
    private String name;
}
