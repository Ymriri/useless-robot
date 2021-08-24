package pers.wuyou.robot.core;

import pers.wuyou.robot.common.StringPool;
import pers.wuyou.robot.exception.ObjectNotFoundException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成并保存实例对象的类
 *
 * @author wuyou
 */
public class ClassUtil {
    protected static final Map<String, Object> LISTENER_INSTANCE_MAP = new HashMap<>();
    protected static final Map<String, Object> OTHER_INSTANCE_MAP = new HashMap<>();
    protected static final Map<String, Class<?>> LISTENER_CLASS_MAP = new HashMap<>();
    protected static final Map<String, Class<?>> OTHER_CLASS_MAP = new HashMap<>();
    protected static String[] listenerPackages;
    private ClassUtil() {
    }

    public static Method getMethod(Class<?> cls, String methodName) {

        Method method = null;
        for (Method m : cls.getMethods()) {
            if (methodName.equals(m.getName())) {
                method = m;
            }
        }
        if (method == null) {
            throw new ObjectNotFoundException("在类" + cls.getSimpleName() + "中未找到" + methodName + "方法");
        }
        return method;
    }

    public static Object getInstance(String type, String clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (StringPool.LISTENER.equals(type)) {
            for (String listenerPackage : listenerPackages) {
                String classPath = listenerPackage + "." + clazz;
                if (LISTENER_INSTANCE_MAP.get(classPath) == null) {
                    Class<?> cls = getClass(classPath);
                    Object o = cls.newInstance();
                    LISTENER_INSTANCE_MAP.put(classPath, o);
                    return o;
                } else {
                    return LISTENER_INSTANCE_MAP.get(classPath);
                }
            }
        }
        if (OTHER_INSTANCE_MAP.get(clazz) == null) {
            Class<?> cls = getClass(clazz);
            Object o = cls.newInstance();
            OTHER_INSTANCE_MAP.put(clazz, o);
            return o;
        } else {
            return OTHER_INSTANCE_MAP.get(clazz);
        }
    }

    public static Class<?> getClass(String clazz) throws ClassNotFoundException {
        return getClass("", clazz);
    }

    public static Class<?> getClass(String type, String clazz) throws ClassNotFoundException {
        if (StringPool.LISTENER.equals(type)) {
            for (String listenerPackage : listenerPackages) {
                String classPath = listenerPackage + "." + clazz;
                if (LISTENER_CLASS_MAP.get(classPath) == null) {
                    Class<?> cls = Class.forName(classPath);
                    return LISTENER_CLASS_MAP.computeIfAbsent(classPath, k -> cls);
                } else {
                    return LISTENER_CLASS_MAP.get(classPath);
                }
            }
        }
        if (OTHER_CLASS_MAP.get(clazz) == null) {
            Class<?> cls = Class.forName(clazz);
            return OTHER_CLASS_MAP.computeIfAbsent(clazz, k -> cls);
        }
        return OTHER_CLASS_MAP.get(clazz);
    }

}
