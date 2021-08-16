package pers.wuyou.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.wuyou.robot.entity.ListenerValues;
import pers.wuyou.robot.web.vo.ListenerValuesVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-04
 */
public interface ListenerValuesService extends IService<ListenerValues> {
    /**
     * 根据name插入字段
     *
     * @param listenerValues 里面包含了name和字段值
     */
    void saveByName(ListenerValuesVO listenerValues);
}
