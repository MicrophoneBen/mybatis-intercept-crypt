package intercept.crypt.resolver;

import intercept.crypt.handler.CryptHandlerFactory;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 有注解的方法加密处理者
 *
 * @author kamjin1996
 * @date 2019-08-01 13:11
 */
@AllArgsConstructor
public class AnnotationMethodEncryptResolver implements MethodEncryptResolver {

    private List<MethodAnnotationEncryptParameter> methodAnnotationEncryptParameterList;

    @Override
    public Object processEncrypt(Object param) {
        Map map = (Map) param;
        methodAnnotationEncryptParameterList.forEach(
                item ->
                        map.computeIfPresent(
                                item.getParamName(),
                                (key, oldValue) ->
                                        CryptHandlerFactory.getCryptHandler(
                                                        oldValue, item.getCryptField())
                                                .encrypt(oldValue, item.getCryptField())));
        return param;
    }
}
