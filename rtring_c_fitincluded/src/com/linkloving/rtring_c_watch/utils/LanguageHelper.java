package com.linkloving.rtring_c_watch.utils;

import java.util.Locale;

public class LanguageHelper
{
	/**
	 * 是否简体中文.
	 * 
	 * @return
	 */
	public static boolean isChinese_SimplifiedChinese()
	{
		String l = Locale.getDefault().getLanguage();
		System.out.println("[DEBUG] 此设备的语言是(getLanguage)："+l);
		// 中文 且 国家是大陆的就表示是简体中文了
		return (l != null && l.toLowerCase().equals("zh")) // 中文（包括简体中文、繁体中文）
				&& CountryHelper.isChina();// 地区是大陆的
	}
}
