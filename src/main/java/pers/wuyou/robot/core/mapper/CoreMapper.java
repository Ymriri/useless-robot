package pers.wuyou.robot.core.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author wuyou
 */
@Mapper
public interface CoreMapper {
    /**
     * 获取所有监听器
     * @return 监听器列表
     */
    List<Map<String, Object>> getListeners();
}
