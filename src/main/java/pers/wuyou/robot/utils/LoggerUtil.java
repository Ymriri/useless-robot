package pers.wuyou.robot.utils;

import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 *
 * @author wuyou
 */
public class LoggerUtil {
    public static void info(String msg) {
        StackTraceElement stackTrace = new Exception().getStackTrace()[1];
        String classname = stackTrace.getClassName();
        String methodName = stackTrace.getMethodName();
        LoggerFactory.getLogger(classname + "." + methodName).info(msg);
    }

    public static void err(String msg) {
        StackTraceElement stackTrace = new Exception().getStackTrace()[1];
        String classname = stackTrace.getClassName();
        String methodName = stackTrace.getMethodName();
        LoggerFactory.getLogger(classname + "." + methodName).error(msg);
    }
}
