package intercept.crypt.handler;

import intercept.crypt.annotation.CryptField;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理 List 对象的加解密
 *
 * @author kamjin1996
 * @date 2019-07-31 11:40
 */
public class ListCryptHandler implements CryptHandler<List> {

    @Override
    public Object encrypt(List list, CryptField cryptField) {
        if (!needCrypt(list)) {
            return list;
        }
        return list.stream()
                .map(
                        item ->
                                CryptHandlerFactory.getCryptHandler(item, cryptField)
                                        .encrypt(item, cryptField))
                .collect(Collectors.toList());
    }

    @Override
    public Object decrypt(List param, CryptField cryptField) {
        if (!needCrypt(param)) {
            return param;
        }
        return param.stream()
                .map(
                        item ->
                                CryptHandlerFactory.getCryptHandler(item, cryptField)
                                        .decrypt(item, cryptField))
                .collect(Collectors.toList());
    }

    private boolean needCrypt(List list) {
        return list != null && list.size() != 0;
    }
}
