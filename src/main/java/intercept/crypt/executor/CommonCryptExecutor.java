package intercept.crypt.executor;

import intercept.crypt.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @auther: kamjin1996
 * @date: 14:13 2019-07-31
 * @description: 普通加密解密执行者
 */
@Slf4j
public class CommonCryptExecutor implements CryptExecutor {

    @Override
    public String encrypt(String str) {
        return CryptUtil.encrypt(str);
    }

    @Override
    public String decrypt(String str) {
        return CryptUtil.decrypt(str);
    }
}
