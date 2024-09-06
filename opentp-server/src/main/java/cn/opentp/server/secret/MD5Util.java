package cn.opentp.server.secret;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * md5加密
 */
public class MD5Util {

    /**
     * todo 后续可配置
     */
    public static String salt = "opentp";

    /**
     * 加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        return DigestUtils.md5Hex(text + salt);
    }

    /**
     * 验证
     *
     * @param text 明文
     * @param md5  密文
     * @return true/false
     */
    public static boolean verify(String text, String md5) {
        //根据传入的密钥进行验证
        return md5(text).equalsIgnoreCase(md5);
    }

    public static void main(String[] args) {
        System.out.println(md5("123456"));
        System.out.println(verify("123456", "273348ddb0f00cab8c5f0833cbeaddcb1af76605"));
    }
}