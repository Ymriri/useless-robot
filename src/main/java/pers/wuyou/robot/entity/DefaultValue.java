package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2021-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段值
     */
    private String value;

    /**
     * 字段描述
     */
    private String description;


}
