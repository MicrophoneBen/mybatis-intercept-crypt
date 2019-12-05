package com.github.kamjin1996.mybatis.intercept.crypt.config;

import java.util.Objects;
import java.util.Optional;

/**
 * 数据库加密配置
 *
 * @author kamjin1996
 */
public class Dbcrypt {

    private static Dbcrypt INSTANCE = new Dbcrypt();

    private AesEnum aes;

    private String secretkey;

    private Boolean enable;

    private Dbcrypt() {
    }

    public Dbcrypt(AesEnum aes, String secretkey) {
        this(aes, secretkey, true);
    }

    public Dbcrypt(AesEnum aes, String secretkey, Boolean enable) {
        check(secretkey, enable, aes);
        this.aes = aes;
        this.secretkey = secretkey;
        this.enable = enable;
        INSTANCE = this;
    }

    public static Optional<Dbcrypt> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    public AesEnum getAes() {
        return aes;
    }

    public void setAes(AesEnum aes) {
        this.aes = aes;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    private static void check(String secretkey, Boolean enable, AesEnum aes) {
        if (Objects.isNull(secretkey)) {
            throw new IllegalArgumentException("secretkey not be null");
        }
        if (Objects.isNull(enable)) {
            throw new IllegalArgumentException("enable not be null");
        }
        if (Objects.isNull(aes)) {
            throw new IllegalArgumentException("aes not be null");
        }
    }


}
