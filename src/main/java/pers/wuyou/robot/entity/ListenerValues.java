package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ListenerValues implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字段id
     */
    private Integer valueId;

    /**
     * 字段值
     */
    private String value;


    public ListenerValues(Integer valueId, String value) {
        this.valueId = valueId;
        this.value = value;
    }
}
