package intercept.crypt.resolver;

/**
 * 表示方法不需要加密
 *
 * @author kamjin1996
 * @date 2019-08-01 13:12
 */
public class EmptyMethodEncryptResolver implements MethodEncryptResolver {

    @Override
    public Object processEncrypt(Object param) {
        return param;
    }
}
