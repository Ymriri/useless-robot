package pers.wuyou.robot.common;

/**
 * 字符串常量池
 *
 * @author wuyou
 */
public interface StringPool {
    String LISTENER = "listener";
    String GROUP_CODE = "groupCode";
    String QQ_CODE = "qqCode";
    String[] GROUP_CODE_PARAMETER = new String[]{"group", "fromGroup", GROUP_CODE};
    String[] QQ_CODE_PARAMETER = new String[]{"qq", "fromQQ", QQ_CODE};
    String[] BOT_CODE_PARAMETER = new String[]{"bot", "botCode", "thisCode"};
    String[] MESSAGE_PARAMETER = new String[]{"message", "msg"};
    String AT = "at";
    String NAME = "name";
    String LOAD_CONFIG = "载入配置";
}
