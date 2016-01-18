package com.active.qa.automation.web.testrunner.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tchen on 1/18/2016.
 */
public class CryptoUtil {
  public static final String AES = "AES";
  private static final String KEY = "CA67C6C94D75A9CF08CC7E7697E32298";

  public static String generateKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance(AES);
    keyGen.init(128);
    SecretKey sk = keyGen.generateKey();
    byte[] b = sk.getEncoded();
    String key = byteArrayToHexString(b);

    return key;
  }

  public static String encrypt(String value) {
    return encrypt(value, KEY);
  }

  /**
   * encrypt a value and generate a key
   *
   * @param value
   * @param key
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static String encrypt(String value, String key) {
    try {
      SecretKeySpec sks = getSecretKeySpec(key);
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
      byte[] encrypted = cipher.doFinal(value.getBytes());
      return byteArrayToHexString(encrypted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String decrypt(String value) {
    return decrypt(value, KEY);
  }

  /**
   * decrypt a value
   *
   * @throws GeneralSecurityException
   * @throws IOException
   */
  public static String decrypt(String value, String keyString) {
    try {
      SecretKeySpec sks = getSecretKeySpec(keyString);
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.DECRYPT_MODE, sks);
      byte[] decrypted = cipher.doFinal(hexStringToByteArray(value));
      return new String(decrypted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private static SecretKeySpec getSecretKeySpec(String keyString) {
    byte[] key = hexStringToByteArray(keyString);
    return new SecretKeySpec(key, AES);
  }

  private static String byteArrayToHexString(byte[] b) {
    StringBuffer sb = new StringBuffer(b.length * 2);
    for (int i = 0; i < b.length; i++) {
      int v = b[i] & 0xff;
      if (v < 16) {
        sb.append('0');
      }
      sb.append(Integer.toHexString(v));
    }
    return sb.toString().toUpperCase();
  }

  private static byte[] hexStringToByteArray(String s) {
    byte[] b = new byte[s.length() / 2];
    for (int i = 0; i < b.length; i++) {
      int index = i * 2;
      int v = Integer.parseInt(s.substring(index, index + 2), 16);
      b[i] = (byte) v;
    }
    return b;
  }
}
