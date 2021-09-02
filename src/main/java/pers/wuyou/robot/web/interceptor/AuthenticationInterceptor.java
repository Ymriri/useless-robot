package pers.wuyou.robot.web.interceptor;


import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.wuyou.robot.common.AccountInfo;
import pers.wuyou.robot.constants.Constant;
import pers.wuyou.robot.web.annotation.LoginRequired;
import pers.wuyou.robot.web.common.UserException;
import pers.wuyou.robot.web.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Token验证过滤器,判断是否已登录
 *
 * @author wuyou
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否需要登录
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        // 有 @LoginRequired 注解，需要认证
        if (methodAnnotation != null) {
            // 判断是否存在令牌信息，如果存在，则允许登录
            String token = request.getHeader("token");
            if (null == token) {
                throw new UserException(UserException.Type.USER_NOT_LOGIN, "User not login");
            }
            AccountInfo accountInfo = JwtUtil.verify(token);

            if (accountInfo == null) {
                throw new UserException(UserException.Type.USER_NOT_FOUND, "User not found");
            }
            request.setAttribute(Constant.CURRENT_USER, accountInfo);
            return true;
        }
        return true;
    }


}