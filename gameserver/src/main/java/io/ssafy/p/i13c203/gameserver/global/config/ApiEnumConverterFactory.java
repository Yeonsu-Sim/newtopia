package io.ssafy.p.i13c203.gameserver.global.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class ApiEnumConverterFactory implements ConverterFactory<String, Enum> {

    // enumClass -> (keyLower -> enumConstant) 캐시
    private static final Map<Class<?>, Map<String, Enum<?>>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> (T) convertInternal(targetType, source);
    }

    private Enum<?> convertInternal(Class<?> enumType, String raw) {
        if (raw == null) return null;
        String key = raw.trim();
        if (key.isEmpty()) return null;
        String keyLower = key.toLowerCase();

        var map = CACHE.computeIfAbsent(enumType, this::buildIndex);
        var hit = map.get(keyLower);
        if (hit != null) return hit;

        throw new IllegalArgumentException("Invalid value '" + raw + "' for enum " + enumType.getSimpleName());
    }

    private Map<String, Enum<?>> buildIndex(Class<?> enumType) {
        // 지원하는 키 우선순위:
        // 1) json()       -> "context", "economy", "asc" 등 (권장)
        // 2) getKey()/getDescription() 등 흔한 패턴
        // 3) name()       -> "CONTEXT", "ECONOMY", "ASC" (fallback)
        Method json = findMethod(enumType, "json");
        Method getKey = findMethod(enumType, "getKey");
        Method getDescription = findMethod(enumType, "getDescription");

        return Stream.of(enumType.getEnumConstants())
                .map(e -> (Enum<?>) e)
                .collect(ConcurrentHashMap::new, (m, e) -> {
                    // json()
                    if (json != null) putSafe(m, invokeString(json, e), e);
                    // getKey()
                    if (getKey != null) putSafe(m, invokeString(getKey, e), e);
                    // getDescription()
                    if (getDescription != null) putSafe(m, invokeString(getDescription, e), e);
                    // name()
                    putSafe(m, e.name(), e);
                }, ConcurrentHashMap::putAll);
    }

    private static Method findMethod(Class<?> type, String name) {
        try {
            var m = type.getMethod(name);
            return (m.getReturnType() == String.class) ? m : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static void putSafe(Map<String, Enum<?>> map, String val, Enum<?> e) {
        if (val == null) return;
        map.putIfAbsent(val.toLowerCase(), e);
    }

    private static String invokeString(Method m, Enum<?> e) {
        try {
            Object v = m.invoke(e);
            return v == null ? null : v.toString();
        } catch (Exception ex) {
            return null;
        }
    }
}

