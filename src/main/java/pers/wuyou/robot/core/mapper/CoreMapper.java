package pers.wuyou.robot.core.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CoreMapper {
    List<Map<String, Object>> getListeners();

    List<Map<String, Object>> getFilters();
}
