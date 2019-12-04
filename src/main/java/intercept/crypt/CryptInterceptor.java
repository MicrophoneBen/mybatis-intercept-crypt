package intercept.crypt;

import intercept.crypt.config.Dbcrypt;
import intercept.crypt.resolver.MethodCryptMetadata;
import intercept.crypt.resolver.MethodCryptMetadataBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加解密插件
 *
 * @author kamjin1996
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
            BoundSql.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@Slf4j
@Data
public class CryptInterceptor implements Interceptor {

    private static boolean switchCrypt;

    private static final String TARGET_FIELD = "id";

    /**
     * 存储源对象和新对象
     */
    public static final ConcurrentHashMap<Object, Object> OLD_AND_NEW_OBJ_MAP = new ConcurrentHashMap<>();

    /**
     * 需加解密处理方法的信息
     */
    private static final ConcurrentHashMap<String, MethodCryptMetadata> METHOD_ENCRYPT_MAP = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (isSwitchCrypt()) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement)args[0];
            Method runningMethod = getMethod(mappedStatement.getId());
            MethodCryptMetadata methodCryptMetadata = getCachedMethodCryptMetaData(mappedStatement, runningMethod);
            args[1] = methodCryptMetadata.encrypt(args[1]);
            Object returnValue = invocation.proceed();
            fixSourceObjNotCanAssignment();
            return methodCryptMetadata.decrypt(returnValue);
        } else {
            return invocation.proceed();
        }
    }

    private MethodCryptMetadata getCachedMethodCryptMetaData(MappedStatement mappedStatement, Method runningMethod) {
        return METHOD_ENCRYPT_MAP.computeIfAbsent(mappedStatement.getId(),
            id -> new MethodCryptMetadataBuilder(runningMethod).build());
    }

    /**
     * 根据statementId获取本次运行的方法
     *
     * @return Method
     */
    private Method getMethod(String statementId) throws ClassNotFoundException {
        final Class clazz = Class.forName(statementId.substring(0, statementId.lastIndexOf(".")));
        final String methodName = statementId.substring(statementId.lastIndexOf(".") + 1);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static boolean isSwitchCrypt() {
        try {
            // 由于当前插件初始化时，spring并未初始化，导致无法获取到值;
            if (!switchCrypt) {
                switchCrypt = Dbcrypt.getDbCryptEnable();
            }
        } catch (IllegalArgumentException e) {
            // 忽略
        }
        return switchCrypt;
    }

    /**
     * 清理
     */
    private static void clearObjMap() {
        OLD_AND_NEW_OBJ_MAP.clear();
    }

    /**
     * 修复selectKey无法赋值给源对象(源对象被clone,因为需要避免重复加密)
     */
    private static void fixSourceObjNotCanAssignment() {
        if (!OLD_AND_NEW_OBJ_MAP.isEmpty()) {
            try {
                Iterator<Map.Entry<Object, Object>> iterator = OLD_AND_NEW_OBJ_MAP.entrySet().iterator();
                boolean isDeal = Boolean.FALSE;
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> next = iterator.next();
                    Object sourceObj = next.getKey();
                    Object cloneObj = next.getValue();

                    Field targetField = sourceObj.getClass().getDeclaredField(TARGET_FIELD);
                    if (targetField != null) {
                        targetField.setAccessible(Boolean.TRUE);
                        Field cloneObjField = cloneObj.getClass().getDeclaredField(TARGET_FIELD);
                        cloneObjField.setAccessible(Boolean.TRUE);
                        Object cloneObjFieldVal = cloneObjField.get(cloneObj);
                        if (Objects.nonNull(cloneObjFieldVal)) {
                            isDeal = Boolean.TRUE;
                            targetField.set(sourceObj, cloneObjFieldVal);
                        }
                    }
                }
                if (isDeal) {
                    clearObjMap();
                }
            } catch (Exception e) {
                log.error("fix bean id the method running failed", e);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
