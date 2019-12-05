package com.github.kamjin1996.mybatis.intercept.crypt.config;

/**
 * Aes算法枚举
 *
 * @author kamjin1996
 */
public enum AesEnum {

    /**
     * 标准与密钥长度、轮数
     */
    AES128("AES-128", 128, 16, 10),

    AES192("AES-192", 192, 24, 12),

    AES256("AES-256", 256, 32, 14);

    private String standard;

    private int standSupport;

    private int secretKeyLength;

    private int round;

    public String getStandard() {
        return standard;
    }

    public int getStandSupport() {
        return standSupport;
    }

    public int getSecretKeyLength() {
        return secretKeyLength;
    }

    public int getRound() {
        return round;
    }

    AesEnum(String standard, int standSupport, int secretKeyLength, int round) {
        this.standard = standard;
        this.standSupport = standSupport;
        this.secretKeyLength = secretKeyLength;
        this.round = round;
    }
}
