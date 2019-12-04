package intercept.crypt.util;

import intercept.crypt.config.Dbcrypt;
import intercept.crypt.exception.InterceptRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * 加解密工具类
 *
 * @author kamjin1996
 */
@Slf4j
@Data
@SuppressWarnings("all")
public class CryptUtil {

    private static final String CRYPT_WAY = "AES";
    private static final String ALGORITHM_MODE_COMPLEMENT = "AES/ECB/PKCS5Padding"; // 算法/模式/补码方式
    private static final String BYTE_CONTROL = "utf-8";
    private static final int STANDARD_SUPPORT = 192; // 加密标准支持：128/192/256 对应key长度分别为16/24/32
    private static final int KEY_LENGTH = 24;

    private static final String KEY_NOT_BE_NULL = "KEY不能为空";
    private static final String KEY_LENGTH_NOT_SUPPORT = "KEY长度不符合";

    private static final String SECURE_RANDOM_INSTANCE_NAME = "SHA1PRNG";

    private static final Set<Class> IGNORE_CLASS = new HashSet<>();

    private static String secretKey = null;

    private static boolean enable = false;

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

    private static String getSecretkey() {
        try {
            if (StringUtils.isBlank(secretKey)) {
                secretKey = Dbcrypt.getDbCryptSecretkey();
            }
        } catch (IllegalArgumentException e) {
            // 忽略
        }
        return secretKey;
    }

    private static boolean isEnable() {
        try {
            if (!enable) {
                enable = Dbcrypt.getDbCryptEnable();
            }
        } catch (IllegalArgumentException e) {
            // 忽略
        }
        return enable;
    }

    public static boolean inIgnoreClass(Class cls) {
        return IGNORE_CLASS.contains(cls);
    }

    public static String encrypt(String sSrc) {
        try {
            return isEnable() ? doEncrypt(sSrc) : sSrc;
        } catch (Exception e) {
            log.info("encrypt str failed,rollback to source str");
        }
        return sSrc;
    }

    public static String decrypt(String sSrc) {
        try {
            return isEnable() ? doDecrypt(sSrc) : sSrc;
        } catch (Exception ex) {
            log.info("decrypt str failed,rollback to source str");
        }
        return sSrc;
    }

    /**
     * 加密
     *
     * @param sSrc
     * @param enable
     * @return
     */
    private static String doEncrypt(String sSrc) throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        String sKey = getSecretkey();
        checkKey(sKey);
        KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);

        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
        secureRandom.setSeed(sKey.getBytes());
        kgen.init(STANDARD_SUPPORT, secureRandom);

        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(enCodeFormat, CRYPT_WAY);

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
     * @param enable
     * @return
     */
    private static String doDecrypt(String sSrc) throws NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        String sKey = getSecretkey();
        checkKey(sKey);

        KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
        secureRandom.setSeed(sKey.getBytes());
        kgen.init(STANDARD_SUPPORT, secureRandom);

        // kgen.init(STANDARD_SUPPORT, new SecureRandom(sKey.getBytes()));
        SecretKeySpec skeySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), CRYPT_WAY);

        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] original = cipher.doFinal(new Base64().decode(sSrc));
        return new String(original, BYTE_CONTROL);
    }

    /**
     * 检查SecretKey
     */
    private static void checkKey(String sKey) {
        if (sKey == null) {
            throw new InterceptRuntimeException(KEY_NOT_BE_NULL);
        }
        if (sKey.length() != KEY_LENGTH) {
            throw new InterceptRuntimeException(KEY_LENGTH_NOT_SUPPORT);
        }
    }
}
