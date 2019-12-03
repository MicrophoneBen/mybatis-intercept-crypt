package intercept.crypt.executor;

/**
 * 特殊加密执行者
 *
 * @author kamjin1996
 * @date 2019-08-01 13:24
 */
public class SpecialCryptExecutor implements CryptExecutor {

    @Override
    public String encrypt(String str) {
        // TODO 特殊加密方式
        return str;
    }

    @Override
    public String decrypt(String str) {
        // TODO 特殊解密方式
        return str;
    }
}
