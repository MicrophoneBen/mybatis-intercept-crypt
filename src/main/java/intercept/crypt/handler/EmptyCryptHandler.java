package intercept.crypt.handler;

import intercept.crypt.annotation.CryptField;

/**
 * 空的加解密执行者，避免过多空指针判断
 *
 * @author kamjin1996
 * @date 2019-07-30 11:40
 */
public class EmptyCryptHandler implements CryptHandler<Object> {

    @Override
    public Object encrypt(Object param, CryptField cryptField) {
        return param;
    }

    @Override
    public Object decrypt(Object param, CryptField cryptField) {
        return param;
    }
}
