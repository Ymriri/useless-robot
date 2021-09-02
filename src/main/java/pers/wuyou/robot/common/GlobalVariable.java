package pers.wuyou.robot.common;

import lombok.Getter;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.message.results.SimpleGroupInfo;
import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.bot.BotManager;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局变量
 *
 * @author wuyou
 */
@SuppressWarnings("unused")
public class GlobalVariable {
    /**
     * 项目路径
     */
    @Getter
    public static final String PROJECT_PATH;
    /**
     * 线程池
     */
    public static final ExecutorService THREAD_POOL;
    /**
     * 缓存群开关
     */
    @Getter
    private static final Map<String, Boolean> BOOT_MAP;
    /**
     * 群成员缓存
     */
    private static final Map<String, AccountInfo> MEMBER_INDEX;
    /**
     * 机器人管理员
     */
    @Getter
    private static final List<String> ADMINISTRATOR;
    /**
     * spring上下文
     */
    @Getter
    private static ConfigurableApplicationContext applicationContext;
    /**
     * 全局BotManager
     */
    @Getter
    private static BotManager botManager;
    /**
     * 全局BotSender
     */
    @Getter
    private static BotSender sender;

    static {
        PROJECT_PATH = System.getProperty("user.dir");
        BOOT_MAP = new HashMap<>();
        MEMBER_INDEX = new HashMap<>();
        ADMINISTRATOR = new ArrayList<>();
        THREAD_POOL = new ThreadPoolExecutor(50, 50, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50), r -> {
            Thread thread = new Thread(r);
            thread.setName("newThread" + thread.getId());
            return thread;
        });
    }

    private GlobalVariable() {
    }

    public static synchronized void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        GlobalVariable.applicationContext = applicationContext;
        GlobalVariable.botManager = applicationContext.getBean(BotManager.class);
        GlobalVariable.sender = GlobalVariable.botManager.getDefaultBot().getSender();
        GlobalVariable.THREAD_POOL.execute(() -> {
            love.forte.simbot.api.sender.Getter getter = GlobalVariable.getter();
            for (SimpleGroupInfo groupInfo : getter.getGroupList()) {
                for (GroupMemberInfo memberInfo : getter.getGroupMemberList(groupInfo.getGroupCode())) {
                    GlobalVariable.addMemberIndex(memberInfo.getAccountCode(), groupInfo.getGroupCode());
                }
            }
        });
    }

    public static synchronized void addMemberIndex(String code, String group) {
        if (GlobalVariable.MEMBER_INDEX.get(code) == null) {
            GlobalVariable.MEMBER_INDEX.put(code, new AccountInfo(code, group));
        } else {
            GlobalVariable.MEMBER_INDEX.get(code).addGroup(group);
        }
    }

    public static synchronized void removeMemberIndex(String code, String group) {
        AccountInfo accountInfo = GlobalVariable.MEMBER_INDEX.get(code);
        if (accountInfo != null) {
            accountInfo.getGroupList().remove(group);
            if (accountInfo.getGroupList().size() == 0) {
                GlobalVariable.MEMBER_INDEX.remove(code);
            }
        }
    }

    public static synchronized AccountInfo getAccountFromMemberIndex(String code) {
        return GlobalVariable.MEMBER_INDEX.get(code);
    }

    public static love.forte.simbot.api.sender.Getter getter() {
        return sender.GETTER;
    }

    public static love.forte.simbot.api.sender.Sender sender() {
        return sender.SENDER;
    }

    public static love.forte.simbot.api.sender.Setter setter() {
        return sender.SETTER;
    }

    public static Boolean isAdministrator(String uin) {
        return ADMINISTRATOR.contains(uin);
    }

    public static String getDefaultBotCode() {
        return botManager.getDefaultBot().getBotInfo().getAccountCode();
    }
}
