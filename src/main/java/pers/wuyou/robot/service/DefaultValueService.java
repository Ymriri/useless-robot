package pers.wuyou.robot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import pers.wuyou.robot.common.StringPool;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.web.vo.DefaultValueVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-04
 */
public interface DefaultValueService extends IService<DefaultValue> {
    /**
     * 多表分页查询
     *
     * @param pageParam page
     * @param name      要搜索的name
     * @return 搜索的关键词列表
     */
    List<DefaultValueVO> selectPage(Page<DefaultValueVO> pageParam, @Param(StringPool.NAME) String name);
}
