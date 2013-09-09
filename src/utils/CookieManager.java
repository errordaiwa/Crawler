package utils;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class CookieManager {
	private static CookieManager cookieManager = new CookieManager();
	private Map<String, Queue<String>> cookies = new ConcurrentHashMap<String, Queue<String>>();
	private Map<String, String> domainToTopLevelDomainMap = new ConcurrentHashMap<String, String>();

	private CookieManager() {
	}

	/**
	 * 
	 * Get a random cookie from given domain
	 * 
	 * @param domainToTopLevelDomainMapparser.reset();parser.reset();parser.reset();parser.reset();parser.reset();parser.reset();parser.reset();
	 * @return cookiesString
	 */
	public String getRandomCookie(String domain) {
		LinkedList<String> domainCookieQueue = (LinkedList<String>) cookies
				.get(getTopLevelDomain(domain));
		if (domainCookieQueue == null || domainCookieQueue.peek() == null) {
			return "";
		}
		int random = (int) (Math.random() * domainCookieQueue.size());
		return domainCookieQueue.get(random);
	}

	/**
	 * Add cookie to cookie list
	 * 
	 * @param domain
	 * @param cookiesString
	 */
	public void addCookies(String domain, String cookiesString) {
		LinkedList<String> domainCookieQueue = (LinkedList<String>) cookies
				.get(getTopLevelDomain(domain));
		if (domainCookieQueue == null) {
			domainCookieQueue = new LinkedList<String>();
			cookies.put(getTopLevelDomain(domain), domainCookieQueue);
		}
		if (!domainCookieQueue.contains(cookiesString)) {
			domainCookieQueue.offer(cookiesString);
		}
		while (domainCookieQueue.size() >= 500) {
			domainCookieQueue.poll();
		}
	}

	/**
	 * Get size of cookie queue belongs to given domain
	 * 
	 * @param domain
	 * @return size
	 */
	public int cookieCount(String domain) {
		Queue<String> domainCookieQueue = cookies
				.get(getTopLevelDomain(domain));
		return domainCookieQueue.size();
	}

	/**
	 * Remove specified cookie belongs to given domain
	 * 
	 * @param domain
	 * @param cookie
	 */
	public void removeCookies(String domain, String cookie) {
		Queue<String> domainCookieQueue = cookies
				.get(getTopLevelDomain(domain));
		domainCookieQueue.remove(cookie);
	}

	/**
	 * Get singleton
	 * 
	 * @return cookieManager
	 */
	public static CookieManager getInstance() {
		return cookieManager;
	}

	/**
	 * Get top domain by given domain.
	 * 
	 * @param domain
	 * @return topLevelDomain
	 */
	public String getTopLevelDomain(String domain) {
		if (domain == null) {
			return null;
		}
		if (!domainToTopLevelDomainMap.containsKey(domain)) {
			String[] splits = domain.split("\\.");
			domainToTopLevelDomainMap.put(domain, (splits[splits.length - 2]
					+ "." + splits[splits.length - 1]));
		}
		return domainToTopLevelDomainMap.get(domain);
	}

}
