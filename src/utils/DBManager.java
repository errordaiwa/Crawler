package utils;

import java.net.UnknownHostException;
import java.util.ArrayList;

import bean.MovieBean;
import bean.PeopleBean;
import bean.UrlBean;
import bean.UserBean;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DBManager {

	private static DBManager dbManager = new DBManager();

	private MongoClient mongoClient;
	private Morphia morphia;
	private Datastore datastoreMovie;
	private Datastore datastorePeople;
	private Datastore datastoreUser;
	private Datastore datastoreUrl;
	private Datastore datastoreUrlTemp;

	//public String URL_DB_NAME = "UrlBeanTemp";
	private final String URL_DB_NAME = "UrlBean2";
	private final String MOVIE_DB_NAME = "MovieBean";
	private final String PEOPLE_DB_NAME = "PeopleBean";
	private final String USER_DB_NAME = "UserBean";
	//public String URL_TEMP_DB_NAME = "UrlBean";
	private final String URL_TEMP_DB_NAME = "UrlBeanTemp2";
	
	private final String URL_COLLECTION_NAME = "UrlBean";
	private final String MOVIE_COLLECTION_NAME = "MovieBean";
	private final String PEOPLE_COLLECTION_NAME = "PeopleBean";
	private final String USER_COLLECTION_NAME = "UserBean";

	private DBCollection movieInfoDBcollection;
	private DBCollection peopleInfoDBcollection;
	private DBCollection userDBcollection;
	private DBCollection urlDBcollection;
	private DBCollection urlTempDBcollection;

	public final static String TYPE_ACTOR = "actor";
	public final static String TYPE_DIRECTOR = "director";

	/**
	 * DBManager provide methods to operate mongoDB
	 */
	private DBManager() {
		try {
			mongoClient = new MongoClient("localhost", 27017);

			morphia = new Morphia();
			morphia.mapPackage("bean");

			datastoreMovie = morphia
					.createDatastore(mongoClient, MOVIE_DB_NAME);
			datastoreMovie.ensureIndexes();
			datastoreMovie.ensureCaps();

			datastorePeople = morphia.createDatastore(mongoClient,
					PEOPLE_DB_NAME);
			datastorePeople.ensureIndexes();
			datastorePeople.ensureCaps();

			datastoreUser = morphia.createDatastore(mongoClient, USER_DB_NAME);
			datastoreUser.ensureIndexes();
			datastoreUser.ensureCaps();

			datastoreUrl = morphia.createDatastore(mongoClient, URL_DB_NAME);
			datastoreUrl.ensureIndexes();
			datastoreUrl.ensureCaps();

			datastoreUrlTemp = morphia.createDatastore(mongoClient,
					URL_TEMP_DB_NAME);
			datastoreUrlTemp.ensureIndexes();
			datastoreUrlTemp.ensureCaps();

			DB db = mongoClient.getDB(URL_DB_NAME);
			urlDBcollection = db.getCollection(URL_COLLECTION_NAME);

			DB db2 = mongoClient.getDB(MOVIE_DB_NAME);
			movieInfoDBcollection = db2.getCollection(MOVIE_COLLECTION_NAME);

			DB db3 = mongoClient.getDB(PEOPLE_DB_NAME);
			peopleInfoDBcollection = db3.getCollection(PEOPLE_COLLECTION_NAME);

			DB db4 = mongoClient.getDB(USER_DB_NAME);
			userDBcollection = db4.getCollection(USER_COLLECTION_NAME);

			DB db5 = mongoClient.getDB(URL_TEMP_DB_NAME);
			urlTempDBcollection = db5.getCollection(URL_COLLECTION_NAME);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static DBManager getInstance() {
		return dbManager;
	}

	/**
	 * Save movieBean to db as maped object
	 * 
	 * @param movieBean
	 */
	public void saveToDB(MovieBean movieBean) {
		DBObject movieOb = new BasicDBObject("_id", movieBean.getMovieUrl());
		if (movieInfoDBcollection.findOne(movieOb) == null)
			datastoreMovie.save(movieBean);
	}

	/**
	 * Save peopleBean to db as maped object
	 * 
	 * @param peopleBean
	 */
	public void saveToDB(PeopleBean peopleBean) {
		DBObject peopleOb = new BasicDBObject("_id", peopleBean.getPeopleUrl());
		if (peopleInfoDBcollection.findOne(peopleOb) == null)
			datastorePeople.save(peopleBean);
	}

	/**
	 * Save userBean to db as maped object
	 * 
	 * @param userBean
	 */
	public void saveToDB(UserBean userBean) {
		DBObject userOb = new BasicDBObject("_id", userBean.getUserUrl());
		if (userDBcollection.findOne(userOb) == null)
			datastoreUser.save(userBean);
	}

	/**
	 * Save urlBean which need to crawl to db as maped object
	 * 
	 * @param urlBean
	 */
	public void saveToDB(UrlBean urlBean) {
		DBObject urlOb = new BasicDBObject("_id", urlBean.getUrl());
		if (urlDBcollection.findOne(urlOb) == null)
			datastoreUrl.save(urlBean);
	}

	/**
	 * Save urlBean which is crawling to temp db
	 * 
	 * @param urlBean
	 */
	public void saveToTempDB(UrlBean urlBean) {
		DBObject urlOb = new BasicDBObject("_id", urlBean.getUrl());
		if (urlTempDBcollection.findOne(urlOb) == null)
			datastoreUrlTemp.save(urlBean);
	}

	/**
	 * delete urlBean which has crawled from temp db
	 * @param urlBean
	 */
	public void deleteFromTempDB(UrlBean urlBean) {
		DBObject urlOb = new BasicDBObject("_id", urlBean.getUrl());
//		urlOb = urlTempDBcollection.findOne(urlOb);
//		if (urlOb == null) {
//			return;
//		}
		urlTempDBcollection.remove(urlOb);

	}

	/**
	 * Use url as a key to check if the movie already exist in db. If exist,
	 * return true
	 * 
	 * @param url
	 * @return
	 */
	public boolean isMovieUrlExist(String url) {
		DBObject movieUrlOb = new BasicDBObject("_id", url);
		if (movieInfoDBcollection.findOne(movieUrlOb) == null)
			return false;
		else
			return true;
	}

	/**
	 * Use url as a key to check if the people already exist in db. If exist,
	 * return true
	 * 
	 * @param url
	 * @return
	 */
	public boolean isPeopleUrlExist(String url) {
		DBObject peopleUrlOb = new BasicDBObject("_id", url);
		if (peopleInfoDBcollection.findOne(peopleUrlOb) == null)
			return false;
		else
			return true;
	}

	/**
	 * Use url as a key to check if the user already exist in db. If exist,
	 * return true
	 * 
	 * @param url
	 * @return
	 */
	public boolean isUserUrlExist(String url) {
		DBObject userUrlOb = new BasicDBObject("_id", url);
		if (userDBcollection.findOne(userUrlOb) == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Get userBean which url match the given parameters from db,
	 * return null if none in db matches
	 * @param url
	 * @return
	 */
	public UserBean getUserBean(String url){
		DBObject searchOb = new BasicDBObject("_id", url);
		
		DBObject userInfoOb = userDBcollection.findOne(searchOb);
		if (userInfoOb == null) {
			return null;
		}
		UserBean userInfo = morphia.fromDBObject(UserBean.class,
				userInfoOb);
		return userInfo;
	}

	/**
	 * Get thr first urlBean from url db and then delete it from db
	 * 
	 * @return
	 */
	public UrlBean popUrl() {
		DBObject urlOb = urlDBcollection.findOne();
		if (urlOb == null) {
			return null;
		}
		UrlBean urlBean = (UrlBean) morphia.fromDBObject(UrlBean.class, urlOb);
		urlDBcollection.remove(urlOb);
		return urlBean;
	}

	/**
	 * Get peopleBean which url match the given parameters from db,
	 * return null if none in db matches
	 * 
	 * @param url
	 * @param type
	 * @return
	 */
	public PeopleBean getPeople(String url) {
		DBObject searchOb = new BasicDBObject("_id", url);

		DBObject peopleInfoOb = peopleInfoDBcollection.findOne(searchOb);
		if (peopleInfoOb == null) {
			return null;
		}
		PeopleBean peopleInfo = morphia.fromDBObject(PeopleBean.class,
				peopleInfoOb);
		return peopleInfo;
	}

	/**
	 * Get MovieBean which url match the given parameters from db,
	 * return null if none in db matches
	 * 
	 * @param url
	 * @return
	 */
	public MovieBean getMovieInfo(String url) {
		DBObject searchOb = new BasicDBObject("_id", url);

		DBObject movieInfoOb = movieInfoDBcollection.findOne(searchOb);
		if (movieInfoOb == null) {
			return null;
		}
		MovieBean movieInfo = morphia.fromDBObject(MovieBean.class, movieInfoOb);
		return movieInfo;
	}

	/**
	 * Return the count of urls left in urls db
	 * 
	 * @return
	 */
	public long getDBcount() {
		return urlDBcollection.count();
	}

	/**
	 * Return the movie list which is acted/directed by the given people
	 * 
	 * @param url
	 * @param peopleType
	 * @return
	 */
	public ArrayList<MovieBean> getMovie(String url, String peopleType) {
		ArrayList<MovieBean> movieList = new ArrayList<MovieBean>();
		DBObject searchOb;
		if (peopleType.equals(TYPE_ACTOR))
			searchOb = new BasicDBObject("actorList.url", url);
		else
			searchOb = new BasicDBObject("directorList.url", url);
		DBCursor movieInfoCursor = movieInfoDBcollection.find(searchOb);
		for (DBObject movieInfoOb : movieInfoCursor.toArray()) {
			MovieBean movieInfo = morphia.fromDBObject(MovieBean.class,
					movieInfoOb);
			movieList.add(movieInfo);
		}
		return movieList;
	}

	/**
	 * Return the movie list which relate with the given people
	 * 
	 * @param url
	 * @param peopleType
	 * @return
	 */
	public ArrayList<MovieBean> getMovie(String url) {
		ArrayList<MovieBean> movieList = new ArrayList<MovieBean>();

		DBObject searchOb = new BasicDBObject("actorList.url", url);
		DBCursor movieInfoCursor = movieInfoDBcollection.find(searchOb);
		for (DBObject movieInfoOb : movieInfoCursor.toArray()) {
			MovieBean movieInfo = morphia.fromDBObject(MovieBean.class,
					movieInfoOb);
			movieList.add(movieInfo);
		}

		searchOb = new BasicDBObject("directorList.url", url);
		movieInfoCursor = movieInfoDBcollection.find(searchOb);
		for (DBObject movieInfoOb : movieInfoCursor.toArray()) {
			MovieBean movieInfo = morphia.fromDBObject(MovieBean.class,
					movieInfoOb);
			movieList.add(movieInfo);
		}
		return movieList;
	}

}