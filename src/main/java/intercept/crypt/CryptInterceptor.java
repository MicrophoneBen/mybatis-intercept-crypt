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
 * @date 2019-07-30 12:49
 */
@Intercepts(
        value = {
            @Signature(
                    type = Executor.class,
                    method = "update",
                    args = {MappedStatement.class, Object.class}),
            @Signature(
                    type = Executor.class,
                    method = "query",
                    args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class,
                        CacheKey.class,
                        BoundSql.class
                    }),
            @Signature(
                    type = Executor.class,
                    method = "query",
                    args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class
                    })
        })
@Slf4j
@Data
public class CryptInterceptor implements Interceptor {

    /** 存储源对象和新对象 */
    public static final ConcurrentHashMap<Object, Object> OLD_AND_NEW_OBJ_MAP =
            new ConcurrentHashMap<>();
    private static final String TARGET_FIELD = "id";
    /** 需加解密处理方法的信息 */
    private static final ConcurrentHashMap<String, MethodCryptMetadata> METHOD_ENCRYPT_MAP =
            new ConcurrentHashMap<>();

    private boolean switchCrypt;

    private boolean isSwitchCrypt() {
        //由于当前插件初始化时，spring并未初始化，导致无法获取到值;
        try {
            switchCrypt = Dbcrypt.getDbCryptEnable();
        } catch (IllegalArgumentException e){
            //忽略
        }
        return switchCrypt;
    }

    /** 清理 */
    private static void clearObjMap() {
        OLD_AND_NEW_OBJ_MAP.clear();
    }

    private static void fixSourceObjNotCanAssignment() {
        if (!OLD_AND_NEW_OBJ_MAP.isEmpty()) {
            try {
                Iterator<Map.Entry<Object, Object>> iterator =
                        OLD_AND_NEW_OBJ_MAP.entrySet().iterator();
                boolean isDeal = Boolean.FALSE;
                while (iterator.hasNext()) {
                    Map.Entry<Object, Object> next = iterator.next();
                    Object souceObj = next.getKey();
                    Object cloneObj = next.getValue();

                    Field targetField = souceObj.getClass().getDeclaredField(TARGET_FIELD);
                    if (targetField != null) {
                        targetField.setAccessible(Boolean.TRUE);
                        Field colneObjField = cloneObj.getClass().getDeclaredField(TARGET_FIELD);
                        colneObjField.setAccessible(Boolean.TRUE);
                        Object cloneObjFieldVal = colneObjField.get(cloneObj);
                        if (Objects.nonNull(cloneObjFieldVal)) {
                            isDeal = Boolean.TRUE;
                            targetField.set(souceObj, cloneObjFieldVal);
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
    public Object intercept(Invocation invocation) throws Throwable {
        if (Objects.nonNull(isSwitchCrypt()) && isSwitchCrypt()) {
            try {
                Object[] args = invocation.getArgs();
                MappedStatement mappedStatement = (MappedStatement) args[0];
                Method method = getMethod(mappedStatement.getId());
                MethodCryptMetadata methodCryptMetadata =
                        getCachedMethodCryptMetaData(mappedStatement, method);
                args[1] = methodCryptMetadata.encrypt(args[1]);
                // 获得出参
                Object returnValue = invocation.proceed();
                // 修复selectKey无法赋值给源对象(源对象被clone,因为需要避免重复加密)
                fixSourceObjNotCanAssignment();
                return methodCryptMetadata.decrypt(returnValue);
            } catch (Exception e) {
                log.info("interceptor crypt or decrypt method running failed",e);
            }
        }
        return invocation.proceed();
    }

    private MethodCryptMetadata getCachedMethodCryptMetaData(
            MappedStatement mappedStatement, Method method) {
        String statementId = mappedStatement.getId();
        return METHOD_ENCRYPT_MAP.computeIfAbsent(
                statementId, id -> new MethodCryptMetadataBuilder(method).build());
    }

    /**
     * 根据statementId获取本次运行的方法
     *
     * @return
     */
    private Method getMethod(String statementId) {
        try {
            final Class clazz =
                    Class.forName(statementId.substring(0, statementId.lastIndexOf(".")));
            final String methodName = statementId.substring(statementId.lastIndexOf(".") + 1);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (ClassNotFoundException e) {
            //
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}
}
