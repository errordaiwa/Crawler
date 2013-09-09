package main;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.Const;
import utils.DBManager;

import bean.PeopleBean;
import bean.UrlBean;

public class PeopleCrawler extends Thread {

	private UrlBean urlBean;
	private String htmlContent;
	private Parser parser;
	private PeopleBean peopleBean;

	// public static void main(String[] args) {
	// UrlBean a = new UrlBean();
	// a.setUrl("http://movie.douban.com/celebrity/1047984/");
	// a.setType("actor");
	// PeopleCrawler b = new PeopleCrawler(a);
	// b.run();
	// }

	public PeopleCrawler(UrlBean urlBean) {
		this.urlBean = urlBean;
		parser = new Parser();
		peopleBean = new PeopleBean();
	}

	public PeopleCrawler(String peopleUrl) {
		this.urlBean = new UrlBean();
		urlBean.setUrl(peopleUrl);
		urlBean.setType(Const.typePeople);
		urlBean.setNeedToSavePeople(false);
		urlBean.setNeedToSaveUser(false);
		parser = new Parser();
		peopleBean = new PeopleBean();
	}

	public void run() {
		DBManager dbManager = DBManager.getInstance();
		String url = urlBean.getUrl();
		url.replace("site", "movie");
		synchronized (dbManager) {
			if (dbManager.isPeopleUrlExist(url)) {
				return;
			}
			dbManager.saveToTempDB(urlBean);
		}
		htmlContent = MovieCrawler.getHtmlContent(url);
		if (htmlContent == null)
			return;
		if(htmlContent == "404"){
			synchronized (dbManager) {
				dbManager.deleteFromTempDB(this.urlBean);
			}
			return;
		}
			

		try {
			// If url contains search, it means we need to parse this url for
			// the real people url
			if (url.contains("search")) {
				NodeFilter urlFilter = new AndFilter(new TagNameFilter("a"),
						new HasAttributeFilter("class", "nbg"));
				parser.setLexer(new Lexer(htmlContent));
				NodeList urlList = (NodeList) parser.parse(urlFilter);
				LinkTag linktag = (LinkTag) urlList.elementAt(0);
				url = linktag.getLink();
				synchronized (dbManager) {
					if (dbManager.isPeopleUrlExist(url)) {
						synchronized (dbManager) {
							dbManager.deleteFromTempDB(this.urlBean);
						}
						return;
					}
				}
				htmlContent = MovieCrawler.getHtmlContent(url);
				if (htmlContent == null)
					return;
			}
			peopleBean.setPeopleUrl(url);
			System.out.println("^^^^^^^^^^ People URL = " + url);
			getMainlyInfo();
			peopleBean.setPeopleName(getPeopleName());
			peopleBean.setHasAward(getAward());

		} catch (Exception e) {
			e.printStackTrace();
			synchronized (dbManager) {
				dbManager.deleteFromTempDB(this.urlBean);
			}
			System.out.println("People info Missing! url is: "
					+ urlBean.getUrl());
		}

		UrlBean urlBean = new UrlBean();
		urlBean.setUrl(url + Const.peopleMovieListUrlTail);
		urlBean.setNeedToSavePeople(false);
		urlBean.setNeedToSaveUser(false);
		urlBean.setType(Const.typePeopleMovieList);

		synchronized (dbManager) {
			if (peopleBean != null)
				dbManager.saveToDB(peopleBean);
			dbManager.saveToDB(urlBean);
			dbManager.deleteFromTempDB(this.urlBean);
		}

		System.out.println(peopleBean.getPeopleUrl() + " Success!");
	}

	private String getPeopleName() throws ParserException {
		parser.setLexer(new Lexer(htmlContent));
		NodeFilter peopleNameFilter = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("id", "content"));
		NodeList peopleNameNodeList = (NodeList) parser.parse(peopleNameFilter);
		HeadingTag headingTag = (HeadingTag) peopleNameNodeList.elementAt(0)
				.getChildren().elementAt(1);
		return MovieCrawler.formateString(headingTag.getStringText());
	}

	private void getMainlyInfo() throws ParserException {
		parser.setLexer(new Lexer(htmlContent));
		NodeFilter infoFilter = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "info"));
		NodeList infoList = (NodeList) parser.parse(infoFilter);
		BulletList bulletList = (BulletList) infoList.elementAt(0)
				.getChildren().elementAt(1);
		for (int i = 0; i < bulletList.getChildCount(); i++) {
			if (bulletList.getChildren().elementAt(i) instanceof Bullet) {
				Bullet bullet = (Bullet) bulletList.getChildren().elementAt(i);
				switch (((Span) bullet.getChild(1)).getStringText()) {
				case "性别":
					String sex = getFormatedInfo(bullet);
					peopleBean.setSex(sex);
					break;
				case "出生日期":
					String birthday = getFormatedInfo(bullet);
					peopleBean.setBirthday(birthday);
					break;
				case "生卒日期":
					String birthdayUnformat = getFormatedInfo(bullet);
					peopleBean.setBirthday(birthdayUnformat.split(" ")[0]);
					break;
				case "出生地":
					String birthplace = getFormatedInfo(bullet);
					peopleBean.setBirthplace(birthplace);
					break;
				case "职业":
					String career = getFormatedInfo(bullet);
					peopleBean.setCareer(career);
					break;
				case "更多外文名":
					String peopleEName = getFormatedInfo(bullet);
					peopleBean.setPeopleEName(peopleEName);
					break;
				case "更多中文名":
					String peopleCName = getFormatedInfo(bullet);
					System.out.println(peopleCName);
					peopleBean.setPeopleCName(peopleCName);
					break;
				}
			}
		}
		parser.reset();
	}

	private boolean getAward() {
		try {
			parser.setLexer(new Lexer(htmlContent));
			NodeFilter scoreFilter = new AndFilter(
					new TagNameFilter("a"),
					new HasAttributeFilter("href", urlBean.getUrl() + "awards/"));
			NodeList awardsNodeList = (NodeList) parser.parse(scoreFilter);
			if (awardsNodeList == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String getFormatedInfo(Bullet bullet) {
		return ((TextNode) bullet.getChild(2)).getText().split("\n")[1].trim();
	}

}
