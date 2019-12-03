package intercept.crypt.executor;

import intercept.crypt.util.CryptUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 普通加解密执行者
 *
 * @author kamjin1996
 * @date 2019-08-01 11:40
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
