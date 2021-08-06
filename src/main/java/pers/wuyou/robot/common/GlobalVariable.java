package pers.wuyou.robot.common;

import love.forte.simbot.api.sender.BotSender;
import love.forte.simbot.bot.BotManager;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 全局变量
 *
 * @author wuyou
 */
public class GlobalVariable {
    /**
     * 缓存群开关
     */
    public static final Map<String, Boolean> BOOT_MAP;
    /**
     * 机器人管理员
     */
    public static final List<String> ADMINISTRATOR;
    /**
     * 线程池
     */
    public static final ExecutorService THREAD_POOL;
    /**
     *  spring上下文
     */
    public static ConfigurableApplicationContext applicationContext;
    /**
     * 全局BotManager
     */
    public static BotManager botManager;
    /**
     * 全局BotSender
     */
    public static BotSender sender;

    static {
        BOOT_MAP = new HashMap<>();
        ADMINISTRATOR = new ArrayList<>();
        THREAD_POOL = new ThreadPoolExecutor(50, 50, 200, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50), r -> {
            Thread thread = new Thread(r);
            thread.setName("newThread" + thread.getId());
            return thread;
        });
    }
public static String getDefaultBotCode(){
        return botManager.getDefaultBot().getBotInfo().getAccountCode();
}
}
