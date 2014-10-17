package cn.edu.swust.utils;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.util.StringUtils;

public class CookieUtil {
	public static BasicCookieStore stringToCookie(String strCookie) throws  Exception{
		if(!StringUtils.hasText(strCookie)){
			strCookie = StaticParameters.defaultCookie;
		}
		String cookies[]=strCookie.split(";");
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie [] cookie= new BasicClientCookie[cookies.length]; 
		for(int i = 0;i<cookies.length;i++){
			String cookieStr[] = cookies[i].split("=");
			try {
				cookie[i] = new BasicClientCookie(cookieStr[0],cookieStr[1]);
			} catch (Exception e) {
				throw new Exception();
			}
		}
		cookieStore.addCookies(cookie);;
		return cookieStore;
	} 
}
