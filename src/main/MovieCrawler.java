package main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.Const;
import utils.DBManager;
import utils.HttpURLConnectionWrapper;
import utils.StrongTag;

import bean.MovieBean;
import bean.People;
import bean.UrlBean;

public class MovieCrawler extends Thread {
	private UrlBean urlBean;

	private Parser movieParser;
	private String htmlContent;

	private DBManager dbManager;

	private static String domain = null;
	
	public static void main(String[] args) {

		MovieCrawler c = new MovieCrawler("http://movie.douban.com/subject/555/");
		c.run();

	}


	public MovieCrawler(UrlBean urlBean) {
		this.urlBean = urlBean;
		movieParser = new Parser();
		dbManager = DBManager.getInstance();
	}
	
	public MovieCrawler(String url) {
		this.urlBean = new UrlBean();
		urlBean.setUrl(url);
		urlBean.setType(Const.typeMovie);
		urlBean.setNeedToSavePeople(true);
		urlBean.setNeedToSaveUser(false);
		movieParser = new Parser();
		dbManager = DBManager.getInstance();
	}

	public void run() {
		try {
			System.out.println("------------ Movie URL = " + urlBean.getUrl());
			MovieBean movieInfo = getMovieinfo();
			if (movieInfo != null) {
				DBManager dbManager = DBManager.getInstance();
				synchronized (dbManager) {
					dbManager.saveToDB(movieInfo);
					dbManager.deleteFromTempDB(urlBean);
				}
				System.out.println(urlBean.getUrl() + " Success!");
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	private MovieBean getMovieinfo() throws ParserException {
		String movieUrl = this.urlBean.getUrl();
		synchronized (dbManager) {
			if (dbManager.isMovieUrlExist(movieUrl)) {
				return null;
			}
			dbManager.saveToTempDB(this.urlBean);
		}
		MovieBean movieInfo = new MovieBean();
		htmlContent = getHtmlContent(movieUrl);
		if (htmlContent == "404") {
			synchronized (dbManager) {
				dbManager.deleteFromTempDB(this.urlBean);
			}
			return null;
		} else if (htmlContent == null)
			return null;
		movieInfo.setMovieUrl(movieUrl);
		movieInfo.setMovieName(getMovieName());
		movieInfo.setActorList(getActorList());
		movieInfo.setDirectorList(getDirectorList());
		movieInfo.setScreenwriterList(getScreenwriterList());
		movieInfo.setType(getType());
		movieInfo.setLocal(getLocal());
		movieInfo.setUpTime(getUpTime());
		movieInfo.setScore(getScore());
		movieInfo.setTag(getTag());
		movieInfo.setRecommendation(getRecommendation());
		movieInfo.setHasAward(getAward());
		if (this.urlBean.isNeedToSaveUser()) {
			saveUser();
		}
		return movieInfo;

	}

	public static String getHtmlContent(String url) {
		for (int i = 0; i < 6; i++) {
//		while(true){
			try {
				StringBuffer html = new StringBuffer();
				URL FormateUrl = new URL(url);
				domain = "http://" + FormateUrl.getHost();
				HttpURLConnectionWrapper httpURLConnection = new HttpURLConnectionWrapper(
						FormateUrl);
				InputStream is = httpURLConnection.getInputStream();
				if (is == null)
					return "404";
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String temp;
				while ((temp = br.readLine()) != null) {
					html.append(temp).append("\n");
				}
				br.close();
				isr.close();
				return html.toString();

			} catch (Exception e) {
				System.out.println("Request denied by server! time = " + i);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}
		return null;
	}

	private String getMovieName() throws ParserException {
		try {
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter movieNameFilter = new AndFilter(
					new TagNameFilter("span"), new HasAttributeFilter(
							"property", "v:itemreviewed"));
			NodeList movieNameList = (NodeList) movieParser
					.parse(movieNameFilter);
			Span spanTag = (Span) movieNameList.elementAt(0);
			String movieName = spanTag.getStringText();
			movieParser.reset();
			return formateString(movieName);
		} catch (Exception e) {
			System.out.println("MovieName Missing!");
			return null;
		}
	}

	private ArrayList<People> getActorList() throws ParserException {
		try {
			ArrayList<People> actorList = new ArrayList<People>();

			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter actorListFilter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("rel", "v:starring"));
			NodeList actorNodeList = (NodeList) movieParser
					.parse(actorListFilter);

			for (int i = 0; i < actorNodeList.size(); i++) {
				LinkTag linkTag = (LinkTag) actorNodeList.elementAt(i);
				People actor = new People();
				actor.setName(linkTag.getStringText());
				actor.setUrl(domain + formateString(linkTag.getLink()));
				actorList.add(actor);

				if (this.urlBean.isNeedToSavePeople()) {
					UrlBean urlBean = new UrlBean();
					urlBean.setUrl(domain + formateString(linkTag.getLink()));
					urlBean.setType(Const.typePeople);
					synchronized (dbManager) {
						dbManager.saveToDB(urlBean);
					}
				}
			}

			movieParser.reset();

			return actorList;
		} catch (Exception e) {
			System.out.println("ActorList Missing!");
			return null;
		}
	}

	private ArrayList<People> getDirectorList() throws ParserException {
		try {
			ArrayList<People> directorList = new ArrayList<People>();

			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter directorListFilter = new AndFilter(
					new TagNameFilter("a"), new HasAttributeFilter("rel",
							"v:directedBy"));
			NodeList directorNodeList = (NodeList) movieParser
					.parse(directorListFilter);

			for (int i = 0; i < directorNodeList.size(); i++) {
				LinkTag linkTag = (LinkTag) directorNodeList.elementAt(i);
				People director = new People();
				director.setName(linkTag.getStringText());
				director.setUrl(domain + formateString(linkTag.getLink()));
				directorList.add(director);

				if (this.urlBean.isNeedToSavePeople()) {
					UrlBean urlBean = new UrlBean();
					urlBean.setUrl(domain + formateString(linkTag.getLink()));
					urlBean.setType(Const.typePeople);
					synchronized (dbManager) {
						dbManager.saveToDB(urlBean);
					}
				}
			}

			movieParser.reset();

			return directorList;
		} catch (Exception e) {
			System.out.println("DirectorList Missing!");
			return null;
		}
	}

	private ArrayList<String> getScreenwriterList() {
		try {
			ArrayList<String> screenwriterList = new ArrayList<String>();

			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter spanListFilter = new AndFilter(
					new TagNameFilter("span"), new NodeFilter() {

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public boolean accept(Node node) {
							if (((Span) node).getStringText().contains("编剧")
									&& ((Span) node).getAttribute("class") == null) {
								return true;
							} else
								return false;
						}
					});

			NodeList spanList = (NodeList) movieParser.parse(spanListFilter);
			Span spanTag = (Span) spanList.elementAt(0);
			for (int i = 0; i < spanTag.getChildCount(); i++) {
				if (spanTag.childAt(i) instanceof LinkTag) {
					screenwriterList.add(((LinkTag) spanTag.childAt(i))
							.getLinkText());
				}
			}

			movieParser.reset();

			return screenwriterList;
		} catch (Exception e) {
			System.out.println("Screenwriter Missing!");
			return null;
		}
	}

	private ArrayList<String> getType() throws ParserException {
		try {
			ArrayList<String> type = new ArrayList<String>();

			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter typeListFilter = new AndFilter(
					new TagNameFilter("span"), new HasAttributeFilter(
							"property", "v:genre"));
			NodeList typeList = (NodeList) movieParser.parse(typeListFilter);

			for (int i = 0; i < typeList.size(); i++) {
				Span spanTag = (Span) typeList.elementAt(i);
				type.add(spanTag.getStringText());
			}

			movieParser.reset();

			return type;
		} catch (Exception e) {
			System.out.println("Type Missing!");
			return null;
		}
	}

	private String getLocal() throws ParserException {
		try {
			NodeFilter infoFilter = null;
			NodeList infoList = null;
			String local = null;
			for (int i = 0; i < 100; i++) {
				movieParser.setLexer(new Lexer(htmlContent));
				infoFilter = new AndFilter(new TagNameFilter("div"),
						new HasAttributeFilter("id", "info"));
				infoList = (NodeList) movieParser.parse(infoFilter);
				if (infoList.elementAt(0) != null) {
					break;
				}
				movieParser.reset();
				if (i / 20 == 0) {
					htmlContent = getHtmlContent(urlBean.getUrl());
				}
				Thread.sleep(3000);
			}
			Div divTag = (Div) infoList.elementAt(0);
			TextNode txtNode = null;
			for (int i = 0; i < divTag.getChildCount(); i++) {
				if (divTag.getChild(i) instanceof Span
						&& ((Span) divTag.getChild(i)).getStringText().equals(
								"制片国家/地区:")) {
					txtNode = (TextNode) divTag.getChild(i + 1);
					break;
				}
			}
			local = txtNode.getText();
			movieParser.reset();
			return local;
		} catch (Exception e) {
			System.out.println("Local Missing!");
			return null;
		}
	}

	private ArrayList<String> getUpTime() throws ParserException {
		try {
			ArrayList<String> uptime = new ArrayList<String>();
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter movieUpTimeFilter = new AndFilter(new TagNameFilter(
					"span"), new HasAttributeFilter("property",
					"v:initialReleaseDate"));
			NodeList upTimeList = (NodeList) movieParser
					.parse(movieUpTimeFilter);
			for (int i = 0; i < upTimeList.size(); i++) {
				Span spanTag = (Span) upTimeList.elementAt(i);
				uptime.add(spanTag.getStringText());
			}
			movieParser.reset();
			return uptime;
		} catch (Exception e) {
			System.out.println("UpTime Missing!");
			return null;
		}
	}

	private String getScore() throws ParserException {
		try {
			movieParser.setLexer(new Lexer(htmlContent));
			PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
			factory.registerTag(new StrongTag());
			movieParser.setNodeFactory(factory);
			NodeFilter scoreFilter = new AndFilter(new TagNameFilter("strong"),
					new HasAttributeFilter("property", "v:average"));
			NodeList scoreList = (NodeList) movieParser.parse(scoreFilter);
			StrongTag tag = (StrongTag) scoreList.elementAt(0);
			String score = tag.getStringText();
			movieParser.reset();
			return score;
		} catch (Exception e) {
			System.out.println("Score Missing!");
			return null;
		}
	}

	private ArrayList<String> getTag() throws ParserException {
		try {
			ArrayList<String> tag = new ArrayList<String>();
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter tagListFilter = new AndFilter(new TagNameFilter("div"),
					new HasAttributeFilter("class", "tags-body"));
			NodeList tagList = (NodeList) movieParser.parse(tagListFilter);
			tagList = tagList.elementAt(0).getChildren();
			for (int i = 0; i < tagList.size(); i++) {
				if (tagList.elementAt(i) instanceof LinkTag) {
					LinkTag linkTag = (LinkTag) tagList.elementAt(i);
					tag.add(linkTag.getStringText().split("<span>")[0]);
				}
			}
			movieParser.reset();
			return tag;
		} catch (Exception e) {
			System.out.println("Tag Missing!");
			return null;
		}
	}

	private ArrayList<String> getRecommendation() throws ParserException {
		try {
			ArrayList<String> recommendation = new ArrayList<String>();
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter recommendationListFilter = new AndFilter(
					new TagNameFilter("div"), new HasAttributeFilter("class",
							"recommendations-bd"));
			NodeList recommendationList = (NodeList) movieParser
					.parse(recommendationListFilter);
			recommendationList = recommendationList.elementAt(0).getChildren();
			for (int i = 0; i < recommendationList.size(); i++) {
				if (recommendationList.elementAt(i) instanceof DefinitionList) {
					LinkTag linkTag = (LinkTag) recommendationList.elementAt(i)
							.getChildren().elementAt(3).getChildren()
							.elementAt(1);
					recommendation.add(linkTag.getStringText());
				}
			}
			movieParser.reset();
			return recommendation;
		} catch (Exception e) {
			System.out.println("Recommendation Missing!");
			return null;
		}
	}

	private boolean getAward() {
		try {
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter scoreFilter = new AndFilter(
					new TagNameFilter("a"),
					new HasAttributeFilter("href", urlBean.getUrl() + "awards/"));
			NodeList awardsNodeList = (NodeList) movieParser.parse(scoreFilter);
			if (awardsNodeList.size() == 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void saveUser() {
		try {
			movieParser.setLexer(new Lexer(htmlContent));
			NodeFilter userFilter = new AndFilter(new TagNameFilter("a"),
					new HasAttributeFilter("class", "others-interest-avatar"));
			NodeList userNodeList = (NodeList) movieParser.parse(userFilter);
			for (int i = 0; i < userNodeList.size(); i++) {
				LinkTag linkTag = (LinkTag) userNodeList.elementAt(i);
				UrlBean urlBean = new UrlBean();
				urlBean.setUrl(linkTag.getLink() + Const.userMovieListUrlTail);
				urlBean.setType(Const.typeUser);
				synchronized (dbManager) {
					dbManager.saveToDB(urlBean);
				}
			}
		} catch (Exception e) {
			System.out.println("Save user failed!");
		}

	}

	public static String formateString(String str) {
		try {
			str = URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
}
