package pers.wuyou.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String groupCode;

    private String qqCode;

    private String message;

    private String answer;


}
