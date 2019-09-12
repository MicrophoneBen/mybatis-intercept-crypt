package intercept.crypt.executor;

import intercept.crypt.annotation.CryptField;
import intercept.crypt.exception.InterceptRuntimeException;

/**
 * 加解密执行者工厂类
 *
 * @author kamjin1996
 * @date 2019-07-31 20:31
 */
public class CryptExecutorFactory {

    private static CryptExecutor COMMEN_HANDLER = new CommonCryptExecutor();
    private static CryptExecutor SPECIAL_HANDLER = new SpecialCryptExecutor();

    /** 根据cryptField中不同的配置 */
    public static CryptExecutor getTypeHandler(CryptField cryptField) {
        CryptExecutor cryptExecutor;
        if (cryptField.value() == CryptType.COMMON) {
            cryptExecutor = COMMEN_HANDLER;
        } else if (cryptField.value() == CryptType.SPECIAL) {
            cryptExecutor = SPECIAL_HANDLER;
        } else {
            throw new InterceptRuntimeException();
        }
        return cryptExecutor;
    }
}
