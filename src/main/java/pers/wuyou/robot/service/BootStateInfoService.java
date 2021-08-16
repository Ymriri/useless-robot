package pers.wuyou.robot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.wuyou.robot.entity.BootStateInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-05
 */
public interface BootStateInfoService extends IService<BootStateInfo> {

    /**
     * 开机或关机
     *
     * @param groupCode 群号
     * @param state     开关机状态
     */
    void bootAndShutDown(String groupCode, boolean state);

}
