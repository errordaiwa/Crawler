package main;

import java.util.ArrayList;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.Const;
import utils.DBManager;

import bean.MovieEvaluate;
import bean.UrlBean;
import bean.UserBean;

public class UserCrawler extends Thread {

	private UrlBean urlBean;
	private DBManager dbManager;
	private UserBean userBean;
	private ArrayList<MovieEvaluate> movieEvaluateList;
	

	public UserCrawler(UrlBean urlBean) {
		this.urlBean = urlBean;
		userBean = new UserBean();
		userBean.setUserUrl(urlBean.getUrl());
		dbManager = DBManager.getInstance();
		movieEvaluateList = new ArrayList<MovieEvaluate>();
	}

	public void run() {
		synchronized(dbManager){
			if(dbManager.isUserUrlExist(urlBean.getUrl())){
				return;
			}
			dbManager.saveToTempDB(urlBean);
		}
		for (int i = 0;; i = i + 15) {
			System.out.println("******** User URL = " + urlBean.getUrl() + i);
			boolean result = parse(urlBean.getUrl() + i);
			if (!result) {
				userBean.setMovieEvaluate(movieEvaluateList);
				synchronized(dbManager){
					dbManager.saveToDB(userBean);
				}
				break;
			}
		}
		
		System.out.println(urlBean.getUrl() + " Success!");
		
		synchronized(dbManager){
			dbManager.deleteFromTempDB(urlBean);
		}
	}

	private boolean parse(String url) {
		Parser parser = new Parser();
		DBManager dbManger = DBManager.getInstance();
		String movieListHtmlContent = MovieCrawler.getHtmlContent(url);
		
		try {
			parser.setLexer(new Lexer(movieListHtmlContent));
			NodeFilter urlFilter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("class", "nbg"));
			NodeList movieUrlNodeList = (NodeList) parser.parse(urlFilter);

			NodeList bulletList = null;
			parser.setLexer(new Lexer(movieListHtmlContent));
			NodeFilter dateFilter = new AndFilter(new TagNameFilter("span"),
					new HasAttributeFilter("class", "date"));
			NodeFilter bulletFilter = new AndFilter(new TagNameFilter("li"),
					new HasChildFilter(dateFilter));
			bulletList = (NodeList) parser.parse(bulletFilter);

			if (movieUrlNodeList.size() == 0) {
				return false;
			}
			for (int i = 0; i < movieUrlNodeList.size(); i++) {
				LinkTag linkTag = (LinkTag) movieUrlNodeList.elementAt(i);
				String movieUrl = linkTag.getLink();
				
				UrlBean urlBean = new UrlBean();
				urlBean.setUrl(movieUrl);
				urlBean.setNeedToSavePeople(false);//TODO true是爬people     false不爬，用户爬去用户观看列表时。
				urlBean.setNeedToSaveUser(false);
				urlBean.setType(Const.typeMovie);
				synchronized (dbManger) {
					dbManger.saveToDB(urlBean);
				}

				Bullet bullet = (Bullet) bulletList.elementAt(i);
				
				MovieEvaluate movieEvaluate = getMovieEvaluate(bullet);
				movieEvaluate.setUrl(movieUrl);
				movieEvaluateList.add(movieEvaluate);
			}
		} catch (ParserException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private MovieEvaluate getMovieEvaluate(Bullet bullet) {
		MovieEvaluate movieEvaluate = new MovieEvaluate();
		for (int i = 0; i < bullet.getChildCount(); i++) {
			if (bullet.getChild(i) instanceof Span) {
				Span spanTag = (Span) bullet.getChild(i);
				if (spanTag.getAttribute("class").equals("date")) {
					movieEvaluate.setRatingTime(spanTag.getStringText());
				} else if (spanTag.getAttribute("class").contains("rating")) {
					movieEvaluate.setRating(getRating(spanTag.getAttribute("class")));
				}
			}
		}
		return movieEvaluate;
	}

	private String getRating(String ratingString) {
		return ratingString.substring(6, 7);
	}
}
