package pers.wuyou.robot.config;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MsgGet;
import pers.wuyou.robot.common.Cat;

/**
 * @author wuyou
 */
public class MessageReplaceConfig {
    private MessageReplaceConfig(){}

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static String reconstructMessage(MsgGet msg, String message) {
        if (msg instanceof GroupMsg) {
            String groupCodeRegex = "\\$\\{groupCode}";
            String groupNameRegex = "\\$\\{groupName}";
            String groupCode = ((GroupMsg) msg).getGroupInfo().getGroupCode();
            String groupName = ((GroupMsg) msg).getGroupInfo().getGroupName();
            message = message
                    .replaceAll(groupCodeRegex, groupCode)
                    .replaceAll(groupNameRegex, groupName == null ? groupCode : groupName)
            ;
        }
        String atBot = "\\$\\{@bot}";
        String atQQ = "\\$\\{@sender}";
        String accountCodeRegex = "\\$\\{accountCode}";
        String accountNameRegex = "\\$\\{accountName}";
        String accountCode = msg.getAccountInfo().getAccountCode();
        String accountName = msg.getAccountInfo().getAccountNickname();
        return message
                .replaceAll(atBot, Cat.atBot())
                .replaceAll(atQQ, Cat.at(accountCode))
                .replaceAll(accountCodeRegex, accountCode)
                .replaceAll(accountNameRegex, accountName == null ? accountCode : accountName)
                .trim();
    }
}
