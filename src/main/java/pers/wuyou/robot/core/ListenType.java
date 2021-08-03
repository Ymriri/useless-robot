package pers.wuyou.robot.core;

/**
 * @author wuyou
 */

public enum ListenType {
    /**
     * 群消息
     */
    GroupMsg,
    /**
     * 私聊消息
     */
    PrivateMsg,
    /**
     * 好友增加
     */
    FriendIncrease,
    /**
     * 群友增加
     */
    GroupMemberIncrease,
    /**
     * 群友减少事件
     */
    GroupMemberReduce,
    /**
     * 好友减少事件
     */
    FriendReduce,
    /**
     * 好友请求事件
     */
    FriendAddRequest,
    /**
     * 群添加请求
     */
    GroupAddRequest,
    /**
     * 私聊消息撤回
     */
    PrivateMsgRecall,
    /**
     * 群聊消息撤回
     */
    GroupMsgRecall,
    /**
     * 群成员权限变动事件
     */
    GroupMemberPermissionChanged,
    /**
     * 群名称变动事件
     */
    GroupNameChanged,
    /**
     * 群友群名片变动事件
     */
    GroupMemberRemarkChanged,
    /**
     * 群友头衔变动事件
     */
    GroupMemberSpecialChanged,
    /**
     * 好友昵称变动事件
     */
    FriendNicknameChanged,
    /**
     * 好友头像变动事件
     */
    FriendAvatarChanged;
    final String packageName = "love.forte.simbot.api.message.events.";

}
