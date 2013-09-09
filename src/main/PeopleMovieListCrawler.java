package main;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.Const;
import utils.DBManager;

import bean.UrlBean;

public class PeopleMovieListCrawler extends Thread {
	private DBManager dbManager = DBManager.getInstance();
	private UrlBean urlBean;
	private boolean needTodelete = true;
	
	 public static void main(String[] args) {
	 UrlBean a = new UrlBean();
	 a.setUrl("http://movie.douban.com/celebrity/1315130/movies?start=");
	 PeopleMovieListCrawler b = new PeopleMovieListCrawler(a);
	 b.run();
	 }

	public PeopleMovieListCrawler(UrlBean urlBean) {
		this.urlBean = urlBean;
	}

	public void run() {
		for (int i = 0;; i = i + 10) {
			System.out.println("~~~~~~~~~ People MovieList URL = "
					+ urlBean.getUrl() + i);
			boolean result = parse(urlBean.getUrl() + i);
			if (!result) {
				break;
			}
		}
		System.out.println(urlBean.getUrl() + " Success!");
	}

	private boolean parse(String url) {
		synchronized (dbManager) {
			dbManager.saveToTempDB(this.urlBean);
		}
		try {
			Parser parser = new Parser();
			String movieListHtmlContent = MovieCrawler.getHtmlContent(url);
			if (movieListHtmlContent != null)
				synchronized (dbManager) {
					dbManager.deleteFromTempDB(this.urlBean);
				}
			parser.setLexer(new Lexer(movieListHtmlContent));
			NodeFilter urlFilter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("class", "nbg"));
			NodeList movieUrlNodeList = (NodeList) parser.parse(urlFilter);

			if (movieUrlNodeList.size() == 0) {
				return false;
			}

			for (int i = 0; i < movieUrlNodeList.size(); i++) {
				LinkTag linkTag = (LinkTag) movieUrlNodeList.elementAt(i);
				String movieUrl = linkTag.getLink();
				UrlBean urlBean = new UrlBean();
				urlBean.setUrl(movieUrl);
				urlBean.setNeedToSavePeople(false);
				urlBean.setNeedToSaveUser(false);
				urlBean.setType(Const.typeMovie);
				synchronized (dbManager) {
					dbManager.saveToDB(urlBean);
				}

			}
		} catch (ParserException e) {
			System.out.println("Parse failed! URL is: " + url);
			return false;
		}
		return true;
	}

}
