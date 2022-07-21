package org.apache.dolphinscheduler.alert.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.Key;

/**
 *
 * 二次开发新增类  告警发送短信
 * @FileName: DesUtils.java
 * @Package:com.ailk.app.common.utils
 * @Description:DES加密和解密工具,可以对字符串进行加密和解密操作
 * @author maozp
 * @date 2012-8-21 下午2:53:44
 *
 */
public class DesUtils {

    /** 字符串默认键值     */
    private static String strDefaultKey = "123456";

    /** 加密工具     */
    private Cipher encryptCipher = null;

    /** 解密工具     */
    private Cipher decryptCipher = null;

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB
     *            需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn
     *            需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static byte[] hexStr2ByteArr(String strIn){
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 默认构造方法，使用默认密钥
     *
     * @throws Exception
     */
    public DesUtils(){
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法
     *
     * @param strKey
     *            指定的密钥
     * @throws Exception
     */
    public DesUtils(String strKey){
        try {
            Key key = getKey(strKey.getBytes());
            encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加密字节数组
     *
     * @param arrB
     *            需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符串
     *
     * @param strIn
     *            需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String strIn){
        String a = null ;
        try {
            a = byteArr2HexStr(encrypt(strIn.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 解密字节数组
     *
     * @param arrB
     *            需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) {
        byte[] a= new byte[0];
        try {
            a = decryptCipher.doFinal(arrB);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return  a;
    }

    /**
     * 解密字符串
     *
     * @param strIn
     *            需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String strIn) {
        String a = null;
        try {
            a = new String(decrypt(hexStr2ByteArr(strIn)),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws java.lang.Exception
     */
    private Key getKey(byte[] arrBTmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

        return key;
    }


    /**
     * main方法  。
     * @author maozp
     * @param args
     */
    public static void main(String[] args) {
//        String test = "2^18115608807^测试";
//      String test = "1^13372029993^处理发送短信的代码的超长字段漏洞处理发送短信的代码的超长字段漏洞2处理发送短信的代码的超长字段漏洞3处理发送短信的代码的超长字段漏洞4处理发送短信的代码的超长字段漏洞5处理发送短信的代码的超长字段漏洞6处理发送短信的代码的超长字段漏洞7处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞9处理发送短信的代码的超长字段漏洞10处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞8处理发送短信的代码的超长字段漏洞20。";
        DesUtils des = new DesUtils();//自定义密钥

//        String test = "2^18115608807^[\"id:167\",\"name:告警测试-56-0-1658392560820\",\"job type: scheduler\",\"state: SUCCESS\",\"recovery:NO\",\"run time: 1\",\"start time: 2022-07-21 16:36:01\",\"end time: 2022-07-21 16:36:07\",\"host: 132.228.27.56:5678\"]";

        String test = "2^18115608807^id:103985;name:告警测试-0-1658387280607;job type: scheduler;state: SUCCESS;recovery:NO;run time: 1;start time: 2022-07-21 15:08:01;end time: 2022-07-21 15:08:04;host: 10.149.1.19:5678;";
//        String test = "2^17625907574^联通帐号测试";
        System.out.println("加密前的字符：" + test);
        System.out.println("加密后的字符：" + des.encrypt(test));
        System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
    }
}
