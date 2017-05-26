package com.linkloving.rtring_c_watch.utils;

import java.util.Locale;


public class CountryHelper
{
	public static boolean isChina()
	{
		String c = Locale.getDefault().getCountry();
		System.out.println("[DEBUG] 此设备的国家是(getCountry)："+c);
		return c != null && c.toLowerCase().equals("cn"); // 大陆是CN、台湾是TW、香港是HK
	}

}
