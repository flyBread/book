package com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;


/**
 * 3des加密解密
 * 
 * @author haoxw
 * @since 2014/6/11
 */
public class ThreeDes {
  private static final Logger logger = LoggerFactory.getLogger(ThreeDes.class);
  private static final String Algorithm = "desede" + "/CBC/PKCS5Padding"; // 定义
  // 加密算法,可用
  // DES,DESede,Blowfish

  final static byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10, 0x40, 0x38, 0x28,
      0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD, 0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30,
      0x40, 0x36, (byte) 0xE2 }; // 24字节的密钥

  // keybyte为加密密钥，长度为24字节
  // src为被加密的数据缓冲区（源）
  public static byte[] encryptMode(byte[] keybyte, byte[] src) {
    try {
      // 生成密钥
      Key deskey = null;
      DESedeKeySpec spec = new DESedeKeySpec(keybyte);
      SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
      deskey = keyfactory.generateSecret(spec);
      // 加密
      Cipher c1 = Cipher.getInstance(Algorithm);
      byte[] iv = new byte[8];
      IvParameterSpec ips = new IvParameterSpec(iv);
      c1.init(Cipher.ENCRYPT_MODE, deskey, ips);
      return c1.doFinal(src);
    } catch (java.security.NoSuchAlgorithmException e1) {
      logger.error("", e1);
    } catch (javax.crypto.NoSuchPaddingException e2) {
      logger.error("", e2);
    } catch (java.lang.Exception e3) {
      logger.error("", e3);
    }
    return null;
  }

  // keybyte为加密密钥，长度为24字节
  // src为加密后的缓冲区
  public static byte[] decryptMode(byte[] keybyte, byte[] src) {
    try {
      // 生成密钥
      Key deskey = null;
      DESedeKeySpec spec = new DESedeKeySpec(keybyte);
      SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
      deskey = keyfactory.generateSecret(spec);
      // 解密
      Cipher c1 = Cipher.getInstance(Algorithm);
      byte[] iv = new byte[8];
      IvParameterSpec ips = new IvParameterSpec(iv);
      c1.init(Cipher.DECRYPT_MODE, deskey, ips);
      return c1.doFinal(src);
    } catch (java.security.NoSuchAlgorithmException e1) {
      logger.error("", e1);
    } catch (javax.crypto.NoSuchPaddingException e2) {
      logger.error("", e2);
    } catch (java.lang.Exception e3) {
      logger.error("", e3);
    }
    return null;
  }

  /**
   * 二行制转字符串
   * 
   * @param b
   * @return
   */
  public static String byte2hex(byte[] b) { // 一个字节的数，
    // 转成16进制字符串
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      // 整数转成十六进制表示
      stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1)
        hs = hs + "0" + stmp;
      else
        hs = hs + stmp;
    }
    return hs.toUpperCase(); // 转成大写
  }

  public static byte[] hex2byte(byte[] b) {
    if ((b.length % 2) != 0) throw new IllegalArgumentException("长度不是偶数");
    byte[] b2 = new byte[b.length / 2];
    for (int n = 0; n < b.length; n += 2) {
      String item = new String(b, n, 2);
      // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
      b2[n / 2] = (byte) Integer.parseInt(item, 16);
    }
    return b2;
  }

  public static String encrypt(String KeyMing) {
    try {
      byte[] encoded = encryptMode(keyBytes, KeyMing.getBytes());
      String str1 = byte2hex(encoded);
      return str1;
    } catch (Exception e) {
      logger.error("", e);
      return null;
    }
  }

  public static String decrypt(String KeyMi) {
    try {
      byte[] encoded = hex2byte(KeyMi.getBytes());
      byte[] srcBytes = decryptMode(keyBytes, encoded);
      return new String(srcBytes);
    } catch (Exception e) {
      logger.error("", e);
      return null;
    }
  }

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    String s1 = encrypt("tplus2016");
    System.out.println("加密后的字符串:" + s1);
    String s2 = decrypt("39B0ED9B464BD9A29C29D2E929EC8A14E4EFD837324C4AAD5C2F5EB67DE6514420047D664F648EB9DA7A4B5AA3AB91F85D46A6F656F32FC07785BEFBDDB907DB1B429C23F30EB549A230C3066AA3927BF06EAD8297BAA104");
    System.out.println("解密后的字符串:" + s2);
  }
}