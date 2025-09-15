package io.ssafy.p.i13c203.gameserver.auth;

import io.ssafy.p.i13c203.gameserver.auth.annotation.CurrentMemberId;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Deprecated
public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> t = parameter.getParameterType();
        boolean isLong = Long.class.isAssignableFrom(t) || long.class.isAssignableFrom(t);
        return parameter.hasParameterAnnotation(CurrentMemberId.class) && isLong;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        return CurrentMemberContext.getRequired();
    }
}