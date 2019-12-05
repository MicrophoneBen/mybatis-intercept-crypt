package com.github.kamjin1996.mybatis.intercept.crypt.util;

import com.github.kamjin1996.mybatis.intercept.crypt.config.Dbcrypt;
import com.github.kamjin1996.mybatis.intercept.crypt.exception.InterceptRuntimeException;
import com.github.kamjin1996.mybatis.intercept.crypt.config.AesEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 加解密工具类
 *
 * @author kamjin1996
 */
@SuppressWarnings("all")
public class CryptUtil {

    private static final Logger log = LoggerFactory.getLogger(CryptUtil.class);

    private static String secretKey = null;
    private static boolean enable = false;
    private static AesEnum aesEnum = null;

    private static final String CRYPT_WAY = "AES";
    private static final String ALGORITHM_MODE_COMPLEMENT = "AES/ECB/PKCS5Padding"; // 算法/模式/补码方式
    private static final String BYTE_CONTROL = "utf-8";
    private static final String SECURE_RANDOM_INSTANCE_NAME = "SHA1PRNG";

    //-----------------------------------------IgnoreClass----------------------------------------------
    private static final Set<Class> IGNORE_CLASS = new HashSet<>();

    static {
        // initIgnoreClass
        IGNORE_CLASS.add(Byte.class);
        IGNORE_CLASS.add(Short.class);
        IGNORE_CLASS.add(Integer.class);
        IGNORE_CLASS.add(Long.class);
        IGNORE_CLASS.add(Float.class);
        IGNORE_CLASS.add(Double.class);
        IGNORE_CLASS.add(Boolean.class);
        IGNORE_CLASS.add(Character.class);
    }

    public static boolean inIgnoreClass(Class cls) {
        return IGNORE_CLASS.contains(cls);
    }
    //-----------------------------------------IgnoreClass----------------------------------------------

    public static String encrypt(String sSrc) {
        try {
            return isEnable() ? doEncrypt(sSrc) : sSrc;
        } catch (Exception e) {
            log.info("encrypt str failed:[{}],rollback to source str:[{}]", e.getMessage(), sSrc);
        }
        return sSrc;
    }

    public static String decrypt(String sSrc) {
        try {
            return isEnable() ? doDecrypt(sSrc) : sSrc;
        } catch (Exception e) {
            log.info("decrypt str failed:[{}],rollback to source str:[{}]", e.getMessage(), sSrc);
        }
        return sSrc;
    }

    /**
     * 加密
     *
     * @param sSrc
     * @return
     */
    private static String doEncrypt(String sSrc) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
        secureRandom.setSeed(checkOrGetDbCryptSecretKey().getBytes());
        kgen.init(checkOrGetDbCryptSupport(), secureRandom);

        byte[] encodeFormat = kgen.generateKey().getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(encodeFormat, CRYPT_WAY);

        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(BYTE_CONTROL));

        // 此处使用BASE64做转码功能，能起到2次加密的作用。
        return new Base64().encodeToString(encrypted);

    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     */
    private static String doDecrypt(String sSrc) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
        secureRandom.setSeed(checkOrGetDbCryptSecretKey().getBytes());

        // kgen.init(checkOrGetDbCryptSupport(), new SecureRandom(sKey.getBytes()));
        kgen.init(checkOrGetDbCryptSupport(), secureRandom);

        SecretKeySpec secretKeySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), CRYPT_WAY);
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] original = cipher.doFinal(new Base64().decode(sSrc));
        return new String(original, BYTE_CONTROL);
    }

    private static int checkOrGetSecretKeyLength() {
        return checkOrGetAesEnum().getSecretKeyLength();
    }

    private static String checkOrGetDbCryptSecretKey() {
        String secretkey = getSecretkey();
        checkKey(secretkey);
        return secretkey;
    }

    private static int checkOrGetDbCryptSupport() {
        return checkOrGetAesEnum().getStandSupport();
    }

    private static AesEnum checkOrGetAesEnum() {
        AesEnum aesEnum = getAesEnum();
        if (Objects.isNull(aesEnum)) {
            throw new InterceptRuntimeException("dbcrypt initialized faild");
        }
        return aesEnum;
    }

    private static AesEnum getAesEnum() {
        return assignment(x -> aesEnum = x.getAes()) ? aesEnum : null;
    }

    private static String getSecretkey() {
        return assignment(x -> secretKey = x.getSecretkey()) ? secretKey : StringUtils.EMPTY;
    }

    private static boolean isEnable() {
        return assignment(x -> enable = x.getEnable()) ? enable : false;
    }

    private static boolean assignment(Consumer<? super Dbcrypt> consumer) {
        Optional<Dbcrypt> dbcrypt = Dbcrypt.getInstance();
        dbcrypt.ifPresent(consumer);
        return dbcrypt.isPresent();
    }

    /**
     * 检查SecretKey
     */
    private static void checkKey(String sKey) {
        if (Objects.isNull(sKey) || StringUtils.isBlank(sKey)) {
            throw new InterceptRuntimeException("secretkey not blank");
        }
        if (sKey.length() != checkOrGetSecretKeyLength()) {
            throw new InterceptRuntimeException("secretkey length not support,[" + sKey.length() + "]");
        }
    }
}
