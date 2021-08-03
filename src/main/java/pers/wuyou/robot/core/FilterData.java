package pers.wuyou.robot.core;

import lombok.Data;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import pers.wuyou.robot.exception.FilterDataIllegalException;

@Data
public class FilterData {
    GroupMsg groupMsg;
    PrivateMsg privateMsg;

    public FilterData(MsgGet msgGet) {
        if(msgGet instanceof GroupMsg) {
            this.groupMsg = (GroupMsg) msgGet;
        }
        if(msgGet instanceof PrivateMsg) {
            this.privateMsg = (PrivateMsg) msgGet;
        }
    }

    public PrivateMsg getPrivateMsg() {
        if (privateMsg == null) {
            throw new FilterDataIllegalException("监听器数据中没有私聊消息数据");
        }
        return privateMsg;
    }

    public GroupMsg getGroupMsg() {
        if (groupMsg == null) {
            throw new FilterDataIllegalException("监听器数据中没有群聊消息数据");
        }
        return groupMsg;
    }

    public boolean isGroup() {
        return groupMsg != null;
    }

    public boolean isPrivate() {
        return privateMsg != null;
    }
}
