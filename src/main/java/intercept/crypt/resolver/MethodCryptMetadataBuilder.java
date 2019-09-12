package intercept.crypt.resolver;

import intercept.crypt.annotation.CryptField;
import intercept.crypt.util.CryptUtil;
import org.apache.ibatis.annotations.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * MethodCryptMetadata 的建造者
 *
 * @author kamjin1996
 * @date 2019-07-31 13:40
 */
public class MethodCryptMetadataBuilder {

    private static final MethodEncryptResolver EMPTY_ENCRYPT_RESOLVER =
            new EmptyMethodEncryptResolver();
    private static final MethodDecryptResolver EMPTY_DECRYPT_RESOLVER =
            new EmptyMethodDecryptResolver();

    private Method method;

    public MethodCryptMetadataBuilder(Method method) {
        this.method = method;
    }

    public MethodCryptMetadata build() {
        MethodCryptMetadata methodCryptMetadata = new MethodCryptMetadata();
        methodCryptMetadata.setMethodEncryptResolver(buildEncryptResolver(method));
        methodCryptMetadata.setMethodDecryptResolver(buildDecryptResolver(method));
        return methodCryptMetadata;
    }

    private MethodEncryptResolver buildEncryptResolver(Method m) {
        // 如果方法不存在 或 方法没有参数
        if (Objects.isNull(m) || m.getParameters() == null || m.getParameters().length == 0) {
            return EMPTY_ENCRYPT_RESOLVER;
        }

        // 当前方法有加密注解的参数
        List<MethodAnnotationEncryptParameter> methodEncryptParamList = this.getCryptParams(m);

        if (m.getParameters().length == 1 && methodEncryptParamList.size() == 1) {
            // 单参数处理，不含@Param注解的参数
            return new SimpleMethodEncryptResolver(methodEncryptParamList.get(0));
        }
        // 多参数处理,包含@Param注解后的参数以及复杂参数
        if (methodEncryptParamList.size() > 0) {
            return new AnnotationMethodEncryptResolver(methodEncryptParamList);
        }
        return EMPTY_ENCRYPT_RESOLVER;
    }

    private MethodDecryptResolver buildDecryptResolver(Method m) {
        // 方法为空 或 无返回
        if (m == null || m.getReturnType() == Void.class) {
            return EMPTY_DECRYPT_RESOLVER;
        }
        return new SimpleMethodDecryptResolver();
    }

    private List<MethodAnnotationEncryptParameter> getCryptParams(Method m) {
        Parameter[] parameters = m.getParameters();
        if (parameters == null || parameters.length == 0) {
            return new ArrayList<>();
        }

        // 需要加密处理的参数
        List<MethodAnnotationEncryptParameter> paramList = new ArrayList<>();

        final Annotation[][] paramAnnotations = m.getParameterAnnotations();

        // 这里配合Param注解一起使用，因为需要参数名称
        Param param = null;
        CryptField crypt = null;
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            String paramName = parameters[i].getName();

            if (paramAnnotations[i].length > 0) {
                // 遍历当前参数注解,有加密注解的，添加到paramList
                for (Annotation annotation : paramAnnotations[i]) {
                    if (annotation instanceof CryptField) {
                        crypt = (CryptField) annotation;
                    }
                    if (annotation instanceof Param) {
                        param = (Param) annotation;
                    }
                }
            }

            // 有@Param注解时，name必须赋值给参数，且入参形式在mapperStament中表现为map的类型
            if (Objects.nonNull(param)) {
                paramName = param.value();
                // 此时为了防止走单参数处理，构造一个空参
                paramList.add(new MethodAnnotationEncryptParameter());
            }

            // 如果方法入参类型是map 或 IgnoreClass列表中的，则直接忽略
            if (type.isAssignableFrom(Map.class) || CryptUtil.inIgnoreClass(type)) {
                continue;
            }

            // 加密string参数
            if (type.isAssignableFrom(String.class) && Objects.nonNull(crypt)) {
                paramList.add(new MethodAnnotationEncryptParameter(paramName, crypt, type));
                break;
            }

            // 如果是实体类，获得实体类并判断是否有加密注解，有则视为需加密参数
            boolean isEntity =
                    !(type.isAssignableFrom(List.class)
                            || type.isAssignableFrom(Collection.class)
                            || type.isAssignableFrom(Array.class));

            if (isEntity) {
                Field[] declaredFields = type.getDeclaredFields();
                for (Field field : declaredFields) {
                    CryptField cryptField = field.getAnnotation(CryptField.class);
                    if (Objects.nonNull(cryptField)) {
                        paramList.add(new MethodAnnotationEncryptParameter(paramName, crypt, type));
                        break;
                    }
                }
            } else {
                // 是list或collection，array,添加到paramList中
                String name = null;
                if (type.isAssignableFrom(List.class)) {
                    name = getParameterNameOrDefault(paramAnnotations[i], "list");
                } else if (type.isAssignableFrom(Collection.class)) {
                    name = getParameterNameOrDefault(paramAnnotations[i], "collection");
                } else if (type.isArray()) {
                    name = getParameterNameOrDefault(paramAnnotations[i], "array");
                }
                if (Objects.nonNull(name)) {
                    // 如果入参个数大于1，在mybatis中就必须指定@Param
                    if (parameters.length > 1) {
                        name = paramName;
                    }
                    paramList.add(
                            new MethodAnnotationEncryptParameter(
                                    name, null, parameters[i].getType()));
                    // 集合入参比较特殊，需构造一个空的参数，防止集合类型无法映射到param
                    if (Objects.isNull(param)) {
                        paramList.add(new MethodAnnotationEncryptParameter());
                    }
                }
            }
            // 引用置空
            param = null;
            crypt = null;
        }
        return paramList;
    }

    private String getParameterNameOrDefault(Annotation[] annotations, String defaultName) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Param) {
                return ((Param) annotation).value();
            }
        }
        return defaultName;
    }
}
