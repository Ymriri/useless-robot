package pers.wuyou.robot.listener;

import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.core.annotation.Injection;
import pers.wuyou.robot.service.BootStateInfoService;
import pers.wuyou.robot.utils.RandomUtil;
import pers.wuyou.robot.utils.SenderUtil;

/**
 * @author wuyou
 */
@SuppressWarnings("unused")
public class BootListener {
    @Injection
    BootStateInfoService bootStateInfoService;

    public void boot(String groupCode, @DefaultValue("boot_tip") String[] bootTip) {
        bootStateInfoService.bootAndShutDown(groupCode, true);
        SenderUtil.sendGroupMsg(groupCode, bootTip[RandomUtil.getRandom(bootTip.length - 1)]);
    }

    public void shutDown(String groupCode, @DefaultValue("shut_down_tip") String[] shutDownTip) {
        bootStateInfoService.bootAndShutDown(groupCode, false);
        SenderUtil.sendGroupMsg(groupCode, shutDownTip[RandomUtil.getRandom(shutDownTip.length - 1)]);
    }
}
