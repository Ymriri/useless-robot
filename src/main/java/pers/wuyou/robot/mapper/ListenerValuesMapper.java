package pers.wuyou.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.wuyou.robot.entity.ListenerValues;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2021-08-04
 */
@Mapper
public interface ListenerValuesMapper extends BaseMapper<ListenerValues> {
    /**
     * 根据defaultValueId查询动态字段
     *
     * @param id default_value_id
     * @return 返回的字段数组
     */
    List<ListenerValues> selectByDefaultValueId(Integer id);

    /**
     * 获取默认值
     *
     * @param name 字段名
     * @param group 群号
     * @return 对应监听器的字段值, 如果没有的话返回默认字段值
     */
    List<String> getDefaultValue(@Param("name") String name, @Param("group") String group);

}
