package intercept.crypt.config;

import java.util.Objects;

/**
 * 数据库加密配置
 *
 * @auther: kamjin1996
 * @date: 10:22 2019-09-12
 */
public class Dbcrypt {

    private static String dbCryptSecretkey;

    private static Boolean dbCryptEnable;

    private static final String NOT_INITIALIZATION_MSG =
        "Dbcrypt bean not initialize,maybe dbcrypt config not configuration...";

    public static String getDbCryptSecretkey() {
        if (Objects.isNull(dbCryptSecretkey)) {
            throw new IllegalArgumentException(NOT_INITIALIZATION_MSG);
        }
        return dbCryptSecretkey;
    }

    public static Boolean getDbCryptEnable() {
        if (Objects.isNull(dbCryptEnable)) {
            throw new IllegalArgumentException(NOT_INITIALIZATION_MSG);
        }
        return dbCryptEnable;
    }

    public Dbcrypt() {}

    public void setSecretkey(String secretkey) {
        dbCryptSecretkey = secretkey;
    }

    public void setEnable(Boolean enable) {
        dbCryptEnable = enable;
    }

}
