package com.linkloving.rtring_c_watch;

import java.util.Map;

import com.example.android.bluetoothlegatt.utils.LogX;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;

public class SharedPreferencesUtil
{
  private static final String AES_KEY = "odp5789421mobily";
  public static final String SP_KEY_ACCOUNT_NAME = "sp_key_account_name";
  public static final String SP_KEY_USER_PASSWORD = "sp_key_user_password";
  private static final String SP_KEY_LOGIN_REMEMBER_PWD = "sp_key_login_remember_pwd";
  public static final String SP_KEY_KEEP_PASSWORD = "sp_key_keep_password";

  private static SharedPreferences getSharedPreferences(Context context)
  {
    return context.getApplicationContext().getSharedPreferences("app_share", 0);
  }

  public static void saveSharedPreferences(Context context, Map<String, String> map)
  {
    if ((map == null) || (map.isEmpty()))
    {
      return;
    }

    SharedPreferences sp = getSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    for (String key : map.keySet())
    {
      editor.putString(key, (String)map.get(key));
    }
    editor.commit();
  }

  public static void saveSharedPreferences(Context context, String key, String value)
  {
    SharedPreferences sp = getSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(key, value);
    editor.commit();
  }

  public static void saveSharedPreferences(Context context, String key, Boolean value)
  {
    SharedPreferences sp = getSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putBoolean(key, value.booleanValue());
    editor.commit();
  }

  public static String getSharedPreferences(Context context, String key, String defaultValue)
  {
    SharedPreferences sp = getSharedPreferences(context);
    return sp.getString(key, defaultValue);
  }

  public static boolean getSharedPreferences(Context context, String key, Boolean defaultValue)
  {
    SharedPreferences sp = getSharedPreferences(context);
    return sp.getBoolean(key, defaultValue.booleanValue());
  }

  public static void savePwd(Context context, String account, String pwd, boolean isRembPwd)
  {
    if ((TextUtils.isEmpty(account)) || (TextUtils.isEmpty(pwd)))
    {
      return;
    }

    try
    {
      account = AES.encrypt(account, "odp5789421mobily");
      pwd = AES.encrypt(pwd, "odp5789421mobily");
    }
    catch (Exception e)
    {
      LogX.getInstance().e("===AES===", e.getMessage());
    }

    SharedPreferences settings = getSharedPreferences(context);

    SharedPreferences.Editor editor = settings.edit();

    editor.putString("sp_key_account_name", account);

    if (isRembPwd)
    {
      editor.putBoolean("sp_key_login_remember_pwd", isRembPwd);
      editor.putString("sp_key_user_password", pwd);
    }
    else
    {
      editor.putBoolean("sp_key_login_remember_pwd", isRembPwd);
      editor.putString("sp_key_user_password", "");
    }

    editor.commit();
  }

  public static String[] getAccountInfo(Context context)
  {
    SharedPreferences settings = getSharedPreferences(context);

    String userid = "";
    String password = "";

    boolean isRemb = settings.getBoolean("sp_key_login_remember_pwd", false);

    if (!isRemb)
    {
      return null;
    }

    userid = settings.getString("sp_key_account_name", null);
    password = settings.getString("sp_key_user_password", null);

    if ((TextUtils.isEmpty(userid)) || (TextUtils.isEmpty(password)))
    {
      return null;
    }

    try
    {
      userid = AES.decrypt(userid, "odp5789421mobily");
      password = AES.decrypt(password, "odp5789421mobily");
    }
    catch (Exception e)
    {
      LogX.getInstance().e("---AES---", e.getMessage());
    }

    return new String[] { userid, password };
  }

  public static void bindAccount(Context context, EditText editAccount, EditText editPwd, CheckBox checkBox)
  {
    SharedPreferences settings = getSharedPreferences(context);

    String userid = "";
    String password = "";
    editAccount.setText("");
    editPwd.setText("");

    boolean isRemb = settings.getBoolean("sp_key_login_remember_pwd", false);

    userid = settings.getString("sp_key_account_name", null);
    password = settings.getString("sp_key_user_password", null);
    try
    {
      if (!TextUtils.isEmpty(userid))
      {
        userid = AES.decrypt(userid, "odp5789421mobily");
        editAccount.setText(userid);
        editAccount.setSelection(userid.length());
      }

      if ((isRemb) && (!TextUtils.isEmpty(password)))
      {
        password = AES.decrypt(password, "odp5789421mobily");
        editPwd.setText(password);
      }
    }
    catch (Exception e)
    {
      LogX.getInstance().e("---AES---", e.getMessage());
    }

    if (checkBox != null)
    {
      checkBox.setChecked(isRemb);
    }
  }

  public static void clearAccount(Context context)
  {
    SharedPreferences settings = getSharedPreferences(context);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("sp_key_account_name", "");
    editor.putString("sp_key_user_password", "");
    editor.putBoolean("sp_key_login_remember_pwd", false);
    editor.commit();
  }
}