package intercept.crypt.util;

import intercept.crypt.config.Dbcrypt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * 加解密工具类
 *
 * @author kamjin1996
 * @date 2019-07-30 12:49
 */
@Slf4j
@Data
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
        return Dbcrypt.getDbCryptSecretkey();
    }

    private static boolean getEnable() {
        return Dbcrypt.getDbCryptEnable();
    }

    public static boolean inIgnoreClass(Class cls) {
        return IGNORE_CLASS.contains(cls);
    }

    public static String encrypt(String sSrc) {
        return encrypt(sSrc, getEnable());
    }

    public static String decrypt(String sSrc) {
        return decrypt(sSrc, getEnable());
    }

    /**
     * 加密
     *
     * @param sSrc
     * @param enable
     * @return
     */
    private static String encrypt(String sSrc, Boolean enable) {
        if (!enable) {
            return sSrc;
        }
        String sKey = getSecretkey();
        checkKey(sKey);

        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);

            /* linux 要加这三行代码*/
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
        } catch (Exception e) {
            log.info("encrypt str failed,rollback to source str");
            return sSrc;
        }
    }

    /**
     * 解密
     *
     * @param sSrc
     * @param enable
     * @return
     */
    private static String decrypt(String sSrc, Boolean enable) {
        if (!enable) {
            return sSrc;
        }
        String sKey = getSecretkey();
        checkKey(sKey);

        try {
            KeyGenerator kgen = KeyGenerator.getInstance(CRYPT_WAY);
            /* linux 要加这三行代码*/
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_INSTANCE_NAME);
            secureRandom.setSeed(sKey.getBytes());
            kgen.init(STANDARD_SUPPORT, secureRandom);

            // kgen.init(STANDARD_SUPPORT, new SecureRandom(sKey.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(enCodeFormat, CRYPT_WAY);

            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_COMPLEMENT);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 先用base64解密
            byte[] encrypted1 = new Base64().decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, BYTE_CONTROL);
                return originalString;
            } catch (Exception e) {
                log.error("decrypt str failed,rollback to source str");
                return sSrc;
            }
        } catch (Exception ex) {
            log.info("decrypt str failed,rollback to source str");
            return sSrc;
        }
    }

    /**
     * 检查SecretKey
     */
    private static void checkKey(String sKey) {
        if (sKey == null) {
            throw new RuntimeException(KEY_NOT_BE_NULL);
        }
        if (sKey.length() != KEY_LENGTH) {
            throw new RuntimeException(KEY_LENGTH_NOT_SUPPORT);
        }
    }
}
