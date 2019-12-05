package com.github.kamjin1996.mybatis.intercept.crypt;

import com.github.kamjin1996.mybatis.intercept.crypt.config.Dbcrypt;
import com.github.kamjin1996.mybatis.intercept.crypt.resolver.MethodCryptMetadata;
import com.github.kamjin1996.mybatis.intercept.crypt.resolver.MethodCryptMetadataBuilder;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class CryptInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(CryptInterceptor.class);

    private static boolean switchCrypt = false;

    private static final String TARGET_FIELD_ID = "id";

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
            MappedStatement mappedStatement = (MappedStatement) args[0];
            Method runningMethod = getMethod(mappedStatement.getId());
            MethodCryptMetadata methodCryptMetadata = getCachedMethodCryptMetaData(mappedStatement, runningMethod);
            args[1] = methodCryptMetadata.encrypt(args[1]);
            Object returnValue = invocation.proceed();
            this.ifInsertReturnId(mappedStatement.getSqlCommandType());
            return methodCryptMetadata.decrypt(returnValue);
        } else {
            return invocation.proceed();
        }
    }

    private void ifInsertReturnId(SqlCommandType currentCommandType) {
        boolean isInsert = Objects.equals("INSERT", currentCommandType.name());
        if (isInsert) {
            returnIdToSourceBean();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private MethodCryptMetadata getCachedMethodCryptMetaData(MappedStatement mappedStatement, Method runningMethod) {
        return METHOD_ENCRYPT_MAP.computeIfAbsent(mappedStatement.getId(),
                id -> new MethodCryptMetadataBuilder(runningMethod).build());
    }

    private static boolean isSwitchCrypt() {
        Dbcrypt.getInstance().ifPresent(x -> switchCrypt = x.getEnable());
        return switchCrypt;
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

    /**
     * 修复selectKey无法赋值给源对象(源对象被clone,因为需要避免重复加密)
     */
    private static void returnIdToSourceBean() {
        if (!OLD_AND_NEW_OBJ_MAP.isEmpty()) {
            try {
                Iterator<Map.Entry<Object, Object>> iterator = OLD_AND_NEW_OBJ_MAP.entrySet().iterator();
                boolean isDeal = Boolean.FALSE;
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> next = iterator.next();
                    Object sourceObj = next.getKey();
                    Object cloneObj = next.getValue();

                    Field sourceObjFieldId = sourceObj.getClass().getDeclaredField(TARGET_FIELD_ID);
                    if (sourceObjFieldId != null) {
                        sourceObjFieldId.setAccessible(Boolean.TRUE);
                        Field cloneObjFieldId = cloneObj.getClass().getDeclaredField(TARGET_FIELD_ID);
                        cloneObjFieldId.setAccessible(Boolean.TRUE);
                        Object cloneObjFieldIdVal = cloneObjFieldId.get(cloneObj);
                        if (Objects.nonNull(cloneObjFieldIdVal)) {
                            isDeal = Boolean.TRUE;
                            sourceObjFieldId.set(sourceObj, cloneObjFieldIdVal);
                        }
                    }
                }
                if (isDeal) {
                    clearObjMap();
                }
            } catch (Exception e) {
                log.error("fix bean id the method running failed.", e);
            }
        }
    }

    private static void clearObjMap() {
        OLD_AND_NEW_OBJ_MAP.clear();
    }

}
