package pers.wuyou.robot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.web.vo.DefaultValueVO;

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
public interface DefaultValueMapper extends BaseMapper<DefaultValue> {
    /**
     * 搜索默认值
     *
     * @param page page
     * @param name 搜索的name
     * @return DefaultValueVO列表
     */
    List<DefaultValueVO> getDefaultValue(Page<DefaultValueVO> page, String name);
}
