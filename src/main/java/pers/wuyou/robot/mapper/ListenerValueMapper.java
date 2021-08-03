package pers.wuyou.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.wuyou.robot.entity.ListenerValue;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2021-08-02
 */
@Mapper
public interface ListenerValueMapper extends BaseMapper<ListenerValue> {
    /**
     * 获取默认值
     *
     * @param id   监听器id
     * @param name 字段名
     * @return 对应监听器的字段值,如果没有的话返回默认字段值
     */
    List<String> getDefaultValue(@Param("id") Integer id, @Param("name") String name);
}
