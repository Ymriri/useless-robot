package pers.wuyou.robot.common;

import lombok.Getter;
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
public class GlobalVariable {
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
        BOOT_MAP = new HashMap<>();
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
    }

    public static String getDefaultBotCode() {
        return botManager.getDefaultBot().getBotInfo().getAccountCode();
    }
}
