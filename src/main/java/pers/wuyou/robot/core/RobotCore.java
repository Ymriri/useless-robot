package pers.wuyou.robot.core;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.*;
import love.forte.simbot.api.sender.*;
import love.forte.simbot.bot.Bot;
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
import pers.wuyou.robot.common.StringVariable;
import pers.wuyou.robot.core.annotation.DefaultValue;
import pers.wuyou.robot.core.annotation.Injection;
import pers.wuyou.robot.core.annotation.InjectionValue;
import pers.wuyou.robot.entity.ListenerEntity;
import pers.wuyou.robot.exception.*;
import pers.wuyou.robot.mapper.ListenerMapper;
import pers.wuyou.robot.utils.LoggerUtil;
import pers.wuyou.robot.utils.SenderUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuyou
 */
@Configuration
public class RobotCore implements CommandLineRunner {
    public static final Map<Integer, Listener> LISTENER_ID_LIST = new HashMap<>();
    public static final Map<Integer, Listener> LISTENER_ID_LIST_BACKUP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, Listener> SPARE_LISTENER_MAP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, Listener> SPARE_LISTENER_MAP_BACKUP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, List<Listener>> LISTENER_MAP = new HashMap<>();
    public static final Map<Class<? extends MsgGet>, List<Listener>> LISTENER_MAP_BACKUP = new HashMap<>();
    private final ListenerMapper listenerMapper;
    private final ConfigurableApplicationContext applicationContext;
    private final BotSender sender = GlobalVariable.getSender();
    private int loadCount = 1;

    @Autowired
    public RobotCore(ConfigurableApplicationContext applicationContext, ListenerMapper listenerMapper) {
        this.applicationContext = applicationContext;
        this.listenerMapper = listenerMapper;
    }

    @Override
    public void run(String... args) {
        GlobalVariable.setApplicationContext(applicationContext);
        GlobalVariable.getADMINISTRATOR().add("1097810498");
        loadAll();
    }

    public void loadAll() {
        try {
            LISTENER_MAP_BACKUP.putAll(LISTENER_MAP);
//            LISTENER_MAP.clear();
            SPARE_LISTENER_MAP_BACKUP.putAll(SPARE_LISTENER_MAP);
            SPARE_LISTENER_MAP.clear();
            LISTENER_ID_LIST_BACKUP.putAll(LISTENER_ID_LIST);
//            LISTENER_ID_LIST.clear();
            long start = System.currentTimeMillis();
            loadListener();
            AtomicInteger num = new AtomicInteger();
            LISTENER_MAP.values().forEach(i -> num.addAndGet(i.size()));
            LoggerUtil.info("Load config data finish! " + num + " listeners");
            if (loadCount > 1) {
                SenderUtil.sendPrivateMsg(GlobalVariable.getADMINISTRATOR().get(0),
                        "Load config data finish! " + num + " listeners, " +
                                "total time: " + (System.currentTimeMillis() - start) + "ms");
            }
            loadCount++;
        } catch (Exception e) {
            e.printStackTrace();
            if (loadCount > 1) {
                LISTENER_MAP.putAll(LISTENER_MAP_BACKUP);
                SenderUtil.sendPrivateMsg(GlobalVariable.getADMINISTRATOR().get(0), "Load config error: " + e.getMessage());
                return;
            }
            int exitCode = SpringApplication.exit(GlobalVariable.getApplicationContext(), () -> 0);
            System.exit(exitCode);
        }
    }

    /**
     * 验证监听器,将数据库里的监听器转为{@link Listener}Listener实例
     *
     * @param listeners 数据库里监听器对象
     */
    private void verifyListener(List<ListenerEntity> listeners) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        Map<Class<? extends MsgGet>, List<Listener>> newListenerMap = new HashMap<>(32);
        for (ListenerEntity lis : listeners) {
            Listener listener;
            if (LISTENER_ID_LIST_BACKUP.get(lis.getId()) != null && lis.getUpdateTime() == LISTENER_ID_LIST_BACKUP.get(lis.getId()).getUpdateTime()) {
                //数据库里没有改变
                listener = LISTENER_ID_LIST_BACKUP.get(lis.getId());
            } else {
                Object o = ClassUtil.getInstance(StringVariable.LISTENER, lis.getClassName());
                Class<?> cls = ClassUtil.getClass(StringVariable.LISTENER, lis.getClassName());
                injectionField(o, cls);
                String methodName = lis.getMethodName();
                Method method = ClassUtil.getMethod(cls, methodName);
                listener = lis.getListener(o, method);
            }
            for (String type : lis.getType()) {
                Class<? extends MsgGet> msgGet = getMsgGet(type);
                if (lis.getIsSpare()) {
                    if (SPARE_LISTENER_MAP.get(msgGet) != null) {
                        throw new ObjectCountBeyondException("类型" + msgGet.getSimpleName() + "备用监听器数量过多");
                    }
                    SPARE_LISTENER_MAP.put(msgGet, listener);
                } else {
                    newListenerMap.computeIfAbsent(msgGet, k -> new ArrayList<>());
                    newListenerMap.get(msgGet).add(listener);
                    LoggerUtil.info("Load listener: " + msgGet.getSimpleName() + "->" + listener.getClass().getSimpleName() + ":" + listener.getName() + " success");
                }
                LISTENER_ID_LIST.put(listener.getId(), listener);
            }
        }
        LISTENER_MAP.clear();
        LISTENER_MAP.putAll(newListenerMap);
    }

    private void loadListener() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        QueryWrapper<ListenerEntity> wrapper = new QueryWrapper<>();
        List<ListenerEntity> listeners = listenerMapper.selectList(wrapper);
        verifyListener(listeners);
        // 遍历每个监听方法,获取到所有阻断监听
        Map<Class<? extends MsgGet>, List<Integer>> listenerIds = new HashMap<>(16);
        for (Class<? extends MsgGet> msgGet : LISTENER_MAP.keySet()) {
            for (Listener listener : LISTENER_MAP.get(msgGet)) {
                listenerIds.putIfAbsent(msgGet, new ArrayList<>());
                if (listenerIds.get(msgGet).contains(listener.getId())) {
                    throw new MethodAlreadyExistException("监听方法" + listener.getClass().getSimpleName() + ":" + listener.getMethod().getName() + "已经被注册");
                }
                listenerIds.get(msgGet).add(listener.getId());
                for (Integer id : listener.getBreakListeners()) {
                    if (id != -1) {
                        if (LISTENER_ID_LIST.get(id) == null) {
                            throw new ObjectNotFoundException("未找到被阻断的监听器" + id);
                        }
                        if (LISTENER_ID_LIST.get(id).getPriority() >= listener.getPriority()) {
                            throw new PriorityIllegalException("阻断方法" + LISTENER_ID_LIST.get(id).getName() + "优先级低于被阻断方法" + listener.getName());
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends MsgGet> getMsgGet(Object type) throws ObjectNotPresentException {
        try {
            return (Class<? extends MsgGet>) ClassUtil.getClass(ListenType.GroupMsg.packageName + type);
        } catch (ClassNotFoundException e) {
            throw new ObjectNotPresentException(type.toString(), new RuntimeException("监听消息类型配置不正确"));
        }
    }

    @Listen(GroupMsg.class) // 群聊消息
    @Listen(PrivateMsg.class) // 私聊消息
    @Listen(FriendIncrease.class) // 监听好友增加事件
    @Listen(GroupMemberIncrease.class) // 监听群友增加事件
    @Listen(GroupMemberReduce.class) // 监听群友减少事件
    @Listen(FriendReduce.class) // 监听好友减少事件
    @Listen(FriendAddRequest.class) // 监听好友请求事件
    @Listen(GroupAddRequest.class) // 监听群添加请求事件
    @Listen(PrivateMsgRecall.class) // 私聊消息撤回事件
    @Listen(GroupMsgRecall.class) // 监听群聊消息撤回事件
    @Listen(GroupMemberPermissionChanged.class) // 监听群成员权限变动事件
    @Listen(GroupNameChanged.class) // 监听群名称变动事件
    @Listen(GroupMemberRemarkChanged.class) // 监听群友群名片变动事件
    @Listen(GroupMemberSpecialChanged.class) // 监听群友头衔变动事件
    @Listen(FriendNicknameChanged.class) // 监听好友昵称变动事件
    @Listen(FriendAvatarChanged.class) // 监听好友头像变动事件
    @SuppressWarnings("unused")
    public void listener(MsgGet msg, ListenerContext context, AtDetection atDetection, Bot bot) {
        if (msg instanceof PrivateMsg && Objects.equals(msg.getText(), StringVariable.LOAD_CONFIG)) {
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

        if (count == 0) {
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
                result = invoke(listener.getMethod(), listener.getInstance(), msg, context, map.get(listener.getId()), atDetection, bot);
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
        return new ArrayList<>();
    }

    private Object invoke(Method method, Object instance, MsgGet msg, ListenerContext context, Object result, AtDetection atDetection, Bot bot) throws Exception {
        Parameter[] paramTypes = method.getParameters();
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Parameter parameter = paramTypes[i];
            if (parameter.getType() == String[].class && parameter.getAnnotation(DefaultValue.class) != null) {
                String value = parameter.getAnnotation(DefaultValue.class).value();
                args[i] = MessageUtil.getDefaultValue(value);
                continue;
            }
            for (ListenType type : ListenType.values()) {
                Class<?> cls = ClassUtil.getClass(type.packageName + type);
                if (cls == parameter.getType() && cls.isInstance(msg)) {
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
            injectionString(msg, parameter, args, i);
        }
        return method.invoke(instance, args);
    }

    private void injectionString(MsgGet msg, Parameter parameter, Object[] args, int i) {
        if (parameter.getType() == String.class) {
            for (String str : StringVariable.QQ_CODE_PARAMETER) {
                if (str.equals(parameter.getName())) {
                    args[i] = msg.getAccountInfo().getAccountCode();
                }
            }
            for (String str : StringVariable.BOT_CODE_PARAMETER) {
                if (str.equals(parameter.getName())) {
                    args[i] = msg.getBotInfo().getAccountCode();
                }
            }
        }
        if (msg instanceof GroupMsg) {
            if (parameter.getType() == Set.class) {
                args[i] = Cat.getAts((GroupMsg) msg);
            }
            if (parameter.getType() == List.class) {
                args[i] = ((GroupMsg) msg).getMsgContent().getCats(StringVariable.AT);
            }
            if (parameter.getType() == String.class) {
                for (String str : StringVariable.GROUP_CODE_PARAMETER) {
                    if (str.equals(parameter.getName())) {
                        args[i] = ((GroupMsg) msg).getGroupInfo().getGroupCode();
                    }
                }
                for (String str : StringVariable.MESSAGE_PARAMETER) {
                    if (str.equals(parameter.getName())) {
                        args[i] = ((GroupMsg) msg).getMsg();
                    }
                }
            }
            if (parameter.getType() == MessageContent.class) {
                args[i] = ((GroupMsg) msg).getMsgContent();
            }
        }
        if (msg instanceof PrivateMsg) {
            if (parameter.getType() == String.class) {
                for (String str : StringVariable.MESSAGE_PARAMETER) {
                    if (str.equals(parameter.getName())) {
                        args[i] = ((PrivateMsg) msg).getMsg();
                    }
                }
            }
            if (parameter.getType() == MessageContent.class) {
                args[i] = ((PrivateMsg) msg).getMsgContent();
            }
        }
    }

    private void injectionField(Object o, Class<?> cls) {
        for (Field field : cls.getDeclaredFields()) {
            Injection injection = field.getAnnotation(Injection.class);
            InjectionValue injectionValue = field.getAnnotation(InjectionValue.class);
            if (injection != null) {

                Object bean;
                try {
                    bean = GlobalVariable.getApplicationContext().getBean(field.getType());
                    BeanUtil.setFieldValue(o, field.getName(), bean);
                } catch (BeansException e) {
                    e.printStackTrace();
                }
            }
            if (injectionValue != null) {
                String value = GlobalVariable.getApplicationContext().getEnvironment().getProperty(injectionValue.value());
                BeanUtil.setFieldValue(o, field.getName(), value);
            }
        }
    }

}
