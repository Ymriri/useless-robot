package pers.wuyou.robot.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import love.forte.simbot.api.message.assists.Permissions;

/**
 * @author wuyou
 */
@Data
@Builder
@AllArgsConstructor
public class GroupEntity {
    /**
     * 群名称
     */
    private String groupName;
    /**
     * 群号码
     */
    private String groupCode;
    /**
     * 群头像
     */
    private String groupAvatar;
    /**
     * 群人数
     */
    private int groupCount;
    /**
     * bot在群里的权限
     */
    private Permissions permission;
    /**
     * 开关机状态
     */
    private boolean powerSwitch;

}
