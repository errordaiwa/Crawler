package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import bean.UrlBean;

import utils.Const;
import utils.DBManager;

/**
 * Crawl movie info from Douban
 * 
 * @author Xingsu
 */
public class Douban {
	private ThreadPoolExecutor threadPool;
	private DBManager dbManager;
	public static int userLimit = 100;

	public static void main(String[] args) {

		Douban news = new Douban();
		UrlBean urlBean = new UrlBean();
		urlBean.setType(Const.typeUser);
		urlBean.setUrl("http://movie.douban.com/people/60648596/collect?start=");
		DBManager.getInstance().saveToDB(urlBean);
		news.run();

	}

	private Douban() {
		this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
		dbManager = DBManager.getInstance();
	}

	private void run() {
		int flags = 20;
		while (true) {
			if (threadPool.getActiveCount() < 20) {
				synchronized (dbManager) {
					UrlBean urlBean = dbManager.popUrl();
					if (urlBean != null)
						parser(urlBean);
					else
						flags--;
					if (flags <= 0)
						break;
				}
			}
		}
	}

	/**
	 * 
	 * @param urlBean
	 */
	private void parser(UrlBean urlBean) {
		switch (urlBean.getType()) {
		case Const.typeMovie:
			movieUrlHandler(urlBean);
			break;
		case Const.typePeople:
			peopleUrlHandler(urlBean);
			break;
		case Const.typeUser:
			userUrlHandler(urlBean);
			break;
		case Const.typePeopleMovieList:
			peopleMovieListUrlHandler(urlBean);
			break;
		default:
			break;
		}
	}

	private void movieUrlHandler(UrlBean urlBean) {
		MovieCrawler movieCrawler = new MovieCrawler(urlBean);
		threadPool.execute(movieCrawler);

	}

	private void peopleUrlHandler(UrlBean urlBean) {
		PeopleCrawler peopleCrawler = new PeopleCrawler(urlBean);
		threadPool.execute(peopleCrawler);

	}

	private void userUrlHandler(UrlBean urlBean) {
		UserCrawler userCrawler = new UserCrawler(urlBean);
		threadPool.execute(userCrawler);
	}

	private void peopleMovieListUrlHandler(UrlBean urlBean) {
		PeopleMovieListCrawler peopleMovieListCrawler = new PeopleMovieListCrawler(
				urlBean);
		threadPool.execute(peopleMovieListCrawler);

	}

}
