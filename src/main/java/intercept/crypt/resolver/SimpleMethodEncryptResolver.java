package intercept.crypt.resolver;

import intercept.crypt.handler.CryptHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 简单加密处理者
 *
 * @author kamjin1996
 * @date 2019-08-01 13:12
 */
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMethodEncryptResolver implements MethodEncryptResolver {

    private MethodAnnotationEncryptParameter encryptParameter;

    @Override
    public Object processEncrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, encryptParameter.getCryptField()).encrypt(param,
            encryptParameter.getCryptField());
    }
}
