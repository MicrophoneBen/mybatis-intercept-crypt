package intercept.crypt.resolver;

import intercept.crypt.annotation.CryptField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 方法加密注解了的参数
 *
 * @author kamjin1996
 * @date 2019-08-01 11:38
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
class MethodAnnotationEncryptParameter {

    private String paramName;
    private CryptField cryptField;
    private Class cls;
}
