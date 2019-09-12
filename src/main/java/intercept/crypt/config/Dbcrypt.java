package intercept.crypt.config;

import java.util.Objects;

/**
 * @auther: kamjin1996
 * @date: 10:22 2019-09-12
 * @description: 数据库加密
 */
public class Dbcrypt {

    private static String dbCryptSecretkey;

    private static Boolean dbCryptEnable;

    public static String getDbCryptSecretkey(){
        if(Objects.isNull(dbCryptSecretkey)){
            throw new IllegalArgumentException("Dbcrypt bean not initialize,maybe dbcrypt config not configuration...");
        }
        return dbCryptSecretkey;
    }

    public static Boolean getDbCryptEnable(){
        if(Objects.isNull(dbCryptEnable)){
            throw new IllegalArgumentException("Dbcrypt bean not initialize,maybe dbcrypt config not configuration...");
        }
        return dbCryptEnable;
    }

    public Dbcrypt() {
    }

    public void setSecretkey(String secretkey) {
        dbCryptSecretkey = secretkey;
    }

    public void setEnable(Boolean enable) {
        dbCryptEnable = enable;
    }

}
