package intercept.crypt.resolver;

/**
 * 表示方法不需要解密
 *
 * @author kamjin1996
 * @date 2019-08-01 13:12
 */
public class EmptyMethodDecryptResolver implements MethodDecryptResolver {

    @Override
    public Object processDecrypt(Object param) {
        return param;
    }
}
