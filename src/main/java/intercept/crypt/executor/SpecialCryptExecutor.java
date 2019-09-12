package intercept.crypt.executor;

/**
 * @auther: kamjin1996
 * @date: 14:15 2019-07-31
 * @description: 特殊加解密执行者
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
