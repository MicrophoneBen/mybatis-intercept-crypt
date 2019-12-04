package intercept.crypt.resolver;

/**
 * 表示方法不需要加密
 *
 * @author kamjin1996
 */
public class EmptyMethodEncryptResolver implements MethodEncryptResolver {

    @Override
    public Object processEncrypt(Object param) {
        return param;
    }
}
