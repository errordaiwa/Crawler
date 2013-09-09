package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpURLConnectionWrapper {
	
	private String cookie;
	private URL url;
	private HttpURLConnection httpURLConnection;
	private CookieManager cookieManager = CookieManager.getInstance();

	/**
	 * Get html content from given url
	 * @param url
	 * @throws IOException
	 */
	public HttpURLConnectionWrapper(URL url) throws IOException {
		this.url = url;
		httpURLConnection = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		fillRequestHeadField();
	}

	/**
	 * Fill request head field with user agent and cookie
	 */
	private void fillRequestHeadField() {
		httpURLConnection
				.setRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
		httpURLConnection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpURLConnection.setRequestProperty("Accept-Language",
				"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		httpURLConnection.setRequestProperty("Accept-Encoding",
				"GB2312,utf-8;q=0.7,*;q=0.7");
		httpURLConnection.setRequestProperty("Host",
				"http://movie.douban.com/");
		httpURLConnection.setRequestProperty("Cache-Control", "max-age=0");
		httpURLConnection.setRequestProperty("Connection", "keep-alive");
		cookie = cookieManager.getRandomCookie(url.getHost());
		httpURLConnection.setRequestProperty("Cookie",
				cookie);

	}

	/**
	 * Get http input stream
	 */
	public InputStream getInputStream() throws IOException {
		InputStream is = null;
		try {
			is = httpURLConnection.getInputStream();
		} catch (Exception e) {
			System.out.println("Request denied by server!");
		}
		resolveCookies();
		int responseCode = httpURLConnection.getResponseCode();
		// if server return 403, delete current cookie and use another random cookie to retry 
		if (responseCode != 200 && responseCode != 404) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			// delete this cookie from cookie manager
			//TODO
//			if (cookieManager.cookieCount(url.getHost()) >= 40){
//				cookieManager.removeCookies(url.getHost(), cookie);
//			}
			
			try {
				httpURLConnection.disconnect();
				is.close();
			} catch (Exception e) {
			}
			httpURLConnection = (HttpURLConnection) this.url
					.openConnection();
			HttpURLConnection.setFollowRedirects(false);
			fillRequestHeadField();
			is = httpURLConnection.getInputStream();
			resolveCookies();
		}
		if(responseCode == 404){
			return null;
		}
		return is;
	}

	/**
	 * Resolve http header field to get cookie, and save cookie to cookie manager
	 */
	private void resolveCookies() {
//		System.out.println("GET" + cookieManager.getRandomCookie(url.getHost()));
		List<String> setCookies = httpURLConnection.getHeaderFields().get(
				"Set-Cookie");
//		System.out.println("RETURN" + setCookies);
		if (setCookies != null && !setCookies.isEmpty()) {
			for (String setCookie : setCookies) {
				cookieManager.addCookies(url.getHost(), setCookie);
			}
		}
	}
}
