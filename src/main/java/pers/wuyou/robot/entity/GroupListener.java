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
 * @since 2021-08-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupListener implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 群号
     */
    private String groupCode;

    /**
     * 监听器id
     */
    private Integer listenerId;

    /**
     * 是否启用
     */
    private boolean isOpen;


}
