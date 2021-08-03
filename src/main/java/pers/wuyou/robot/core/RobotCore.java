package pers.wuyou.robot.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.Listens;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.*;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.api.sender.Setter;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.filter.AtDetection;
import love.forte.simbot.listener.ListenerContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import pers.wuyou.robot.common.Cat;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.core.annotation.Injection;
import pers.wuyou.robot.core.annotation.InjectionValue;
import pers.wuyou.robot.entity.ListenerEntity;
import pers.wuyou.robot.exception.ObjectCountBeyondException;
import pers.wuyou.robot.exception.ObjectNotPresentException;
import pers.wuyou.robot.mapper.ListenerMapper;
import pers.wuyou.robot.mapper.ListenerValueMapper;
import pers.wuyou.robot.utils.LoggerUtil;
import pers.wuyou.robot.utils.SenderUtil;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static pers.wuyou.robot.common.GlobalVariable.sender;

/**
 * @author wuyou
 */
@Configuration
public class RobotCore implements CommandLineRunner {
    public static final Map<Class<? extends MsgGet>, Listener> SPARE_LISTENER_MAP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, Listener> SPARE_LISTENER_MAP_BACKUP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, List<Listener>> LISTENER_MAP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, List<Listener>> LISTENER_MAP_BACKUP = new HashMap<>();
    private final ListenerMapper listenerMapper;
    private final ListenerValueMapper listenerValueMapper;
    private final ConfigurableApplicationContext applicationContext;
    private int loadCount = 1;


    @Autowired
    public RobotCore(ConfigurableApplicationContext applicationContext, ListenerMapper listenerMapper, ListenerValueMapper listenerValueMapper) {
        this.applicationContext = applicationContext;
        this.listenerMapper = listenerMapper;
        this.listenerValueMapper = listenerValueMapper;
    }

    @Override
    public void run(String... args) {
        GlobalVariable.applicationContext = applicationContext;
        GlobalVariable.botManager = applicationContext.getBean(BotManager.class);
        GlobalVariable.sender = GlobalVariable.botManager.getDefaultBot().getSender();
        GlobalVariable.ADMINISTRATOR.add("1097810498");
        loadAll();
//        applicationContext.getBean(BootClass.class).boot();
    }

    public void loadAll() {
        try {
            LISTENER_MAP_BACKUP.putAll(LISTENER_MAP);
            LISTENER_MAP.clear();
            SPARE_LISTENER_MAP_BACKUP.putAll(SPARE_LISTENER_MAP);
            SPARE_LISTENER_MAP.clear();
            long start = System.currentTimeMillis();
            loadConfig(); // 加载本地项目配置文件
            AtomicInteger num = new AtomicInteger();
            LISTENER_MAP.values().forEach(i -> num.addAndGet(i.size()));
            LoggerUtil.info("Load config data finish! " + num + " listeners");
            if (loadCount > 1) {
                SenderUtil.sendPrivateMsg(GlobalVariable.ADMINISTRATOR.get(0),
                        "Load config data finish! " + num + " listeners, " +
                                "total time: " + (System.currentTimeMillis() - start) + "ms");
            }
            loadCount++;
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                LoggerUtil.err("Not found bot config!");
                return;
            }
            e.printStackTrace();
            if (loadCount > 1) {
                LISTENER_MAP.putAll(LISTENER_MAP_BACKUP); // 如果报错恢复备份配置
                SenderUtil.sendPrivateMsg(GlobalVariable.ADMINISTRATOR.get(0), "Load config error: " + e.getMessage());
                return;
            }
            int exitCode = SpringApplication.exit(GlobalVariable.applicationContext, () -> 0);
            System.exit(exitCode);
        }
    }

    public void loadConfig() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        LoggerUtil.info("Start load config data ...");
        QueryWrapper<ListenerEntity> wrapper = new QueryWrapper<>();
        loadListener(listenerMapper.selectList(wrapper));
    }


    /**
     * 验证监听器,将数据库里的监听器转为{@link Listener}Listener实例
     *
     * @param listeners 数据库里监听器对象
     */
    private void verifyListener(List<ListenerEntity> listeners) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        List<Listener> list = new ArrayList<>();
        for (ListenerEntity info : listeners) {
            Object o = ClassUtil.getInstance(GlobalVariable.LISTENER, info.getClassName());
            Class<?> cls = ClassUtil.getClass(GlobalVariable.LISTENER, info.getClassName());
            injectionField(o, cls);
            String name = info.getName();
            String methodName = info.getMethodName();
            Method method = ClassUtil.getMethod(cls, methodName);
            Listener listener = Listener.builder()
                    .id(info.getId())
                    .instance(o)
                    .method(method)
                    .name(name)
                    .priority(info.getPriority())
                    .breakListeners(info.getBreakListeners())
                    .atBot(info.getAtBot())
                    .atAny(info.getAtAny())
                    .isBoot(info.getIsBoot())
                    .build();
            for (String type : info.getType()) {
                Class<? extends MsgGet> msgGet = getMsgGet(type);
                if (info.getIsSpare()) {
                    if (SPARE_LISTENER_MAP.get(msgGet) != null) {
                        throw new ObjectCountBeyondException("类型" + msgGet.getSimpleName() + "备用监听器数量过多");
                    }
                    SPARE_LISTENER_MAP.put(msgGet, listener);
                } else {
                    LISTENER_MAP.computeIfAbsent(msgGet, k -> new ArrayList<>());
                    LISTENER_MAP.get(msgGet).add(listener);
                    LoggerUtil.info("Load listener: " + msgGet.getSimpleName() + "->" + listener.getClass().getSimpleName() + ":" + listener.getName() + " success");
                }
            }
        }
    }

    private void loadListener(List<ListenerEntity> listeners) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        verifyListener(listeners);
        // 遍历每个监听方法,获取到所有阻断监听
        Map<Class<? extends MsgGet>, List<Integer>> breakListenerIds = new HashMap<>();
        for (Class<? extends MsgGet> msgGet : LISTENER_MAP.keySet()) {
            for (Listener listener : LISTENER_MAP.get(msgGet)) {
                breakListenerIds.putIfAbsent(msgGet, new ArrayList<>());
                breakListenerIds.get(msgGet).addAll(Arrays.asList(listener.getBreakListeners()));
            }
        }
        breakListenerIds.forEach((msgGet, ids) -> {
            ids.removeIf(id -> getListenerById(id) != null || id == -1);
            if (ids.size() > 0) {
                throw new RuntimeException("未找到被阻断的监听器" + ids);
            }
        });
        if (breakListenerIds.size() > 0) {
            Map<Class<? extends MsgGet>, List<Listener>> breakListeners = new HashMap<>();
            LISTENER_MAP.forEach((msgGet, listenerList) -> listenerList.forEach(listener -> breakListenerIds.get(msgGet).forEach(breakListenerId -> {
                if (breakListenerId.equals(listener.getId())) {
                    breakListeners.putIfAbsent(msgGet, new ArrayList<>());
                    breakListeners.get(msgGet).add(listener);
                }
            })));
            LISTENER_MAP.forEach((msgGet, listenerList) -> listenerList.forEach(listener -> {
                if (breakListeners.get(msgGet) != null) {
                    breakListeners.get(msgGet).forEach(breakListener -> {
                        for (Integer id : listener.getBreakListeners()) {
                            if (id.equals(breakListener.getId())) {
                                if (breakListener.getPriority() >= listener.getPriority()) {
                                    throw new RuntimeException("阻断方法" + breakListener.getName() + "优先级低于被阻断方法" + listener.getName());
                                }
                            }
                        }
                    });
                }
            }));
        }

    }

    private Listener getListenerById(int id) {
        for (List<Listener> listeners : LISTENER_MAP.values()) {
            for (Listener listener : listeners) {
                if (listener.getId() == id) {
                    return listener;
                }
            }
        }
        return null;
    }

    private Listener getListenerByMsgGetAndId(Class<? extends MsgGet> msgGet, int id) {
        for (Listener listener : LISTENER_MAP.get(msgGet)) {
            if (listener.getId() == id) {
                return listener;
            }
        }
        return null;
    }

    private Listener[] getListenersByMsgGetAndIds(Class<? extends MsgGet> msgGet, Integer... ids) {
        Listener[] listeners = null;
        for (Listener listener : LISTENER_MAP.get(msgGet)) {
            for (int id : ids) {
                if (listener.getId() == id) {
                    if (listeners == null) {
                        listeners = new Listener[]{listener};
                    } else {
                        listeners = Arrays.copyOf(listeners, listeners.length + 1);
                    }
                }
            }
        }
        return listeners;
    }


    @SuppressWarnings("unchecked")
    private Class<? extends MsgGet> getMsgGet(Object type) throws ObjectNotPresentException {
        try {
            return (Class<? extends MsgGet>) ClassUtil.getClass(ListenType.GroupMsg.packageName + type);
        } catch (ClassNotFoundException e) {
            throw new ObjectNotPresentException(type.toString(), new RuntimeException("监听消息类型配置不正确"));
        }
    }


    @Listens({
            @Listen(GroupMsg.class), // 群聊消息
            @Listen(PrivateMsg.class), // 私聊消息
            @Listen(FriendIncrease.class), // 监听好友增加事件
            @Listen(GroupMemberIncrease.class), // 监听群友增加事件
            @Listen(GroupMemberReduce.class), // 监听群友减少事件
            @Listen(FriendReduce.class), // 监听好友减少事件
            @Listen(FriendAddRequest.class), // 监听好友请求事件
            @Listen(GroupAddRequest.class), // 监听群添加请求事件
            @Listen(PrivateMsgRecall.class), // 私聊消息撤回事件
            @Listen(GroupMsgRecall.class), // 监听群聊消息撤回事件
            @Listen(GroupMemberPermissionChanged.class), // 监听群成员权限变动事件
            @Listen(GroupNameChanged.class), // 监听群名称变动事件
            @Listen(GroupMemberRemarkChanged.class), // 监听群友群名片变动事件
            @Listen(GroupMemberSpecialChanged.class), // 监听群友头衔变动事件
            @Listen(FriendNicknameChanged.class), // 监听好友昵称变动事件
            @Listen(FriendAvatarChanged.class), // 监听好友头像变动事件

    })
    public void listener(MsgGet msg, ListenerContext context, AtDetection atDetection, Bot bot) {
        if (msg instanceof PrivateMsg && Objects.equals(msg.getText(), "载入配置")) {
            loadAll();
            return;
        }
        Map<Integer, Object> map = new HashMap<>(32);
        List<List<Listener>> sortListeners = getAndSortListeners(msg);
        int count;
        count = sortListeners.stream().mapToInt(List::size).sum();
        for (List<Listener> l : sortListeners) {
            for (Listener listener : l) {
                for (Integer breakListener : listener.getBreakListeners()) {
                    // 把阻断方法判断值设置为"null"
                    map.put(breakListener, "null");
                }
                // 如果获取到的对象是"null"的话说明该方法被阻断
                if (map.get(listener.getId()) != "null") {
                    count -= invoke(listener, msg, context, map, atDetection, bot) ? 0 : 1;
                }
            }
        }

        if (count == 0) { // 判断是否执行备用方法
            try {
                for (ListenType type : ListenType.values()) {
                    Class<?> cls = ClassUtil.getClass(type.packageName + type);
                    if (cls.isInstance(msg)) {
                        Listener listener = SPARE_LISTENER_MAP.get(cls);
                        if (listener != null) {
                            invoke(listener, msg, context, map, atDetection, bot);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行方法
     *
     * @return 是否成功执行
     */
    private boolean invoke(Listener listener, MsgGet msg, ListenerContext context, Map<Integer, Object> map, AtDetection atDetection, Bot bot) {
        try {
            Object result;
            if (listener.validation(msg)) {
                result = invoke(listener.getId(), listener.getMethod(), listener.getInstance(), msg, context, map.get(listener.getId()), atDetection, bot);
                // 给该方法的所有阻断方法赋值,不能赋值为"null"
                if (result != null) {
                    for (Integer breakListener : listener.getBreakListeners()) {
                        map.put(breakListener, result);
                    }
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<List<Listener>> getAndSortListeners(MsgGet msg) {
        List<Listener> listeners = getListeners(msg);
        Map<Integer, List<Listener>> listenerMap = new TreeMap<>(Comparator.reverseOrder());
        listeners.forEach(i -> {
            listenerMap.computeIfAbsent(i.getPriority(), k -> new ArrayList<>());
            listenerMap.get(i.getPriority()).add(i);
        });
        return new ArrayList<>(listenerMap.values());
    }

    private List<Listener> getListeners(MsgGet msg) {
        try {
            for (ListenType type : ListenType.values()) {
                Class<?> cls = ClassUtil.getClass(type.packageName + type);
                if (cls.isInstance(msg)) {
                    return LISTENER_MAP.getOrDefault(cls, new ArrayList<>());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private Object invoke(Integer id, Method method, Object instance, MsgGet msg, ListenerContext context, Object result, AtDetection atDetection, Bot bot) throws Exception {
        Parameter[] paramTypes = method.getParameters();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Parameter parameter = paramTypes[i];
            if (parameter.getType() == String.class && parameter.getAnnotation(DefaultValue.class) != null) {
                String value = parameter.getAnnotation(DefaultValue.class).value();
                args[i] = listenerValueMapper.getDefaultValue(id, value);
                System.out.println(value);
                continue;
            }
            for (ListenType type : ListenType.values()) {
                Class<?> cls = ClassUtil.getClass(type.packageName + type);
                if (cls == parameter.getType()) {
                    args[i] = msg;
                }
            }
            if (parameter.getType() == ListenerContext.class) {
                args[i] = context;
            }
            if (parameter.getType() == Sender.class) {
                args[i] = sender.SENDER;
            }
            if (parameter.getType() == Getter.class) {
                args[i] = sender.GETTER;
            }
            if (parameter.getType() == Setter.class) {
                args[i] = sender.SETTER;
            }
            if (parameter.getType() == MsgSender.class) {
                args[i] = sender;
            }
            if (parameter.getType() == AtDetection.class) {
                args[i] = atDetection;
            }
            if (parameter.getType() == Bot.class) {
                args[i] = bot;
            }
            if ("result".equals(parameter.getName())) {
                args[i] = result;
            }
            if (parameter.getType() == String.class) {
                if ("qq".equals(parameter.getName()) || "fromQQ".equals(parameter.getName()) || "qqCode".equals(parameter.getName())) {
                    args[i] = msg.getAccountInfo().getAccountCode();
                }
                if ("botCode".equals(parameter.getName()) || "bot".equals(parameter.getName()) || "thisCode".equals(parameter.getName())) {
                    args[i] = msg.getBotInfo().getAccountCode();
                }
            }
            if (msg instanceof GroupMsg) {
                if (parameter.getType() == Set.class) {
                    args[i] = Cat.getAts((GroupMsg) msg);
                }
                if (parameter.getType() == List.class) {
                    args[i] = ((GroupMsg) msg).getMsgContent().getCats("at");
                }
                if (parameter.getType() == String.class) {
                    if ("group".equals(parameter.getName()) || "fromGroup".equals(parameter.getName()) || "groupCode".equals(parameter.getName())) {
                        args[i] = ((GroupMsg) msg).getGroupInfo().getGroupCode();
                    }
                    if ("message".equals(parameter.getName()) || "msg".equals(parameter.getName())) {
                        args[i] = ((GroupMsg) msg).getMsg();
                    }
                }
                if (parameter.getType() == MessageContent.class) {
                    args[i] = ((GroupMsg) msg).getMsgContent();
                }
            }
            if (msg instanceof PrivateMsg) {
                if (parameter.getType() == String.class) {
                    if ("message".equals(parameter.getName()) || "msg".equals(parameter.getName())) {
                        args[i] = ((PrivateMsg) msg).getMsg();
                    }
                }
                if (parameter.getType() == MessageContent.class) {
                    args[i] = ((PrivateMsg) msg).getMsgContent();
                }
            }

        }
        return method.invoke(instance, args);
    }

    private void injectionField(Object o, Class<?> cls) throws IllegalAccessException {
        for (Field field : cls.getDeclaredFields()) {
            Injection injection = field.getAnnotation(Injection.class);
            InjectionValue injectionValue = field.getAnnotation(InjectionValue.class);
            if (injection != null) {

                Object bean;
                try {
                    bean = GlobalVariable.applicationContext.getBean(field.getType());
                    field.setAccessible(true);
                    field.set(o, bean);
                } catch (BeansException ignored) {
                }
            }
            if (injectionValue != null) {
                String value = GlobalVariable.applicationContext.getEnvironment().getProperty(injectionValue.value());
                field.setAccessible(true);
                field.set(o, value);
            }
        }
    }

}
