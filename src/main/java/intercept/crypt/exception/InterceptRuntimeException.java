package intercept.crypt.exception;

/**
 * 自定义运行时异常，避免太多异常链
 *
 * @author kamjin1996
 * @date 2019-07-31 09:48
 */
public class InterceptRuntimeException extends RuntimeException {

    public InterceptRuntimeException() {}

    public InterceptRuntimeException(String message) {
        super(message);
    }
}
