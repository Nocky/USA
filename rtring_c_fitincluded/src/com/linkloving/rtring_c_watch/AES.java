package com.linkloving.rtring_c_watch;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.example.android.bluetoothlegatt.utils.LogX;
import com.rtring.buiness.util.Base64;

public class AES
{
  private static final String TAG = "===AES===";

  public static String encrypt(String sSrc, String sKey)
    throws Exception
  {
    if (sKey == null)
    {
      return null;
    }

    if (sKey.length() != 16)
    {
      return null;
    }
    byte[] raw = sKey.getBytes();
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
    cipher.init(1, skeySpec, iv);
    byte[] encrypted = cipher.doFinal(sSrc.getBytes());

    return Base64.encode(encrypted);
  }

  public static String decrypt(String sSrc, String sKey)
    throws Exception
  {
    try
    {
      if (sKey == null)
      {
        return null;
      }

      if (sKey.length() != 16)
      {
        return null;
      }
      byte[] raw = sKey.getBytes("ASCII");
      SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec iv = new IvParameterSpec(
        "0102030405060708".getBytes());
      cipher.init(2, skeySpec, iv);
      byte[] encrypted1 = Base64.decode(sSrc);
      try
      {
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);
        return originalString;
      }
      catch (Exception e)
      {
        LogX.getInstance().e("===AES===", e.toString());
        return null;
      }
    }
    catch (Exception ex)
    {
      LogX.getInstance().e("===AES===", ex.toString());
    }return null;
  }
}