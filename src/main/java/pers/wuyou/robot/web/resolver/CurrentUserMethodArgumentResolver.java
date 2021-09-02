package pers.wuyou.robot.web.resolver;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import pers.wuyou.robot.common.AccountInfo;
import pers.wuyou.robot.web.annotation.CurrentUser;
import pers.wuyou.robot.constants.Constant;
import pers.wuyou.robot.web.entity.User;

/**
 * 增加方法注入，将含有 @CurrentUser 注解的方法参数注入当前登录用户
 * @author wuyou
 */
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class)
            && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AccountInfo accountInfo = (AccountInfo) webRequest.getAttribute(Constant.CURRENT_USER, RequestAttributes.SCOPE_REQUEST);
        if (accountInfo != null) {
            return accountInfo;
        }
        throw new MissingServletRequestPartException(Constant.CURRENT_USER);
    }
}