package intercept.crypt.resolver;

import intercept.crypt.handler.CryptHandlerFactory;
import lombok.Getter;

/**
 * 简单解密处理者
 *
 * @author kamjin1996
 */
@Getter
public class SimpleMethodDecryptResolver implements MethodDecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        return CryptHandlerFactory.getCryptHandler(param, null).decrypt(param, null);
    }
}
