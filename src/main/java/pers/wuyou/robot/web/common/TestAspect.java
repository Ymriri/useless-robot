package pers.wuyou.robot.web.common;

import org.apache.http.client.methods.HttpPost;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.List;

/**
 * @author wuyou
 */
@Aspect
@Component
public class TestAspect {
    @SuppressWarnings("unchecked")
    @Around("execution(public * pers.wuyou.robot.web.controller..*(..)))")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        assert sra != null;
        HttpServletRequest request = sra.getRequest();
        if (HttpPost.METHOD_NAME.equals(request.getMethod())) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue;
            }
            if (args[i] instanceof List) {
                List<String> argList = ((List<String>) args[i]);
                for (int j = 0; j < argList.size(); j++) {
                    argList.set(j, URLDecoder.decode(String.valueOf(argList.get(j)), "utf-8"));
                }
            } else if (args[i] instanceof String) {
                args[i] = URLDecoder.decode(String.valueOf(args[i]), "utf-8");
            }
        }
        return joinPoint.proceed(args);
    }

}