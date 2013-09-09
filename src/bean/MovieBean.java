package bean;

import java.util.ArrayList;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity
public class MovieBean {

	@Id
	private String movieUrl;
	private String movieName;

	private ArrayList<People> actorList;
	private ArrayList<People> directorList;
	private ArrayList<String> screenwriterList;

	private ArrayList<String> type;
	private String local;
	private ArrayList<String> upTime;
	private String score;
	private ArrayList<String> tag;
	private ArrayList<String> recommendation;
	private boolean hasAward;

	public String getMovieUrl() {
		return movieUrl;
	}

	public void setMovieUrl(String movieUrl) {
		this.movieUrl = movieUrl;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	
	public ArrayList<People> getActorList() {
		return actorList;
	}

	public void setActorList(ArrayList<People> actorList) {
		this.actorList = actorList;
	}

	public ArrayList<People> getDirectorList() {
		return directorList;
	}

	public void setDirectorList(ArrayList<People> directorList) {
		this.directorList = directorList;
	}

	public ArrayList<String> getScreenwriterList() {
		return screenwriterList;
	}

	public void setScreenwriterList(ArrayList<String> screenwriterList) {
		this.screenwriterList = screenwriterList;
	}

	public ArrayList<String> getType() {
		return type;
	}

	public String getTypeString() {
		StringBuilder typeString = new StringBuilder();
		if (type == null || type.size() == 0) {
			return null;
		}
		typeString.append(type.get(0));
		for (int i = 1; i < type.size(); i++) {
			if (checkForRepeat(type, i)) {
				continue;
			}
			typeString.append(", ");
			typeString.append(type.get(i));
		}
		return typeString.toString();
	}

	public void setType(ArrayList<String> type) {
		this.type = type;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public ArrayList<String> getUpTime() {
		return upTime;
	}

	public String getUpTimeString() {
		StringBuilder upTimeString = new StringBuilder();
		if (upTime == null || upTime.size() == 0) {
			return null;
		}
		upTimeString.append(upTime.get(0));
		for (int i = 1; i < upTime.size(); i++) {
			if (checkForRepeat(upTime, i)) {
				continue;
			}
			upTimeString.append(", ");
			upTimeString.append(upTime.get(i));
		}
		return upTimeString.toString();
	}

	public void setUpTime(ArrayList<String> upTime) {
		this.upTime = upTime;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public ArrayList<String> getTag() {
		return tag;
	}

	public String getTagString() {
		StringBuilder tagString = new StringBuilder();
		if (tag == null || tag.size() == 0) {
			return null;
		}
		tagString.append(tag.get(0));
		for (int i = 1; i < tag.size(); i++) {
			if (checkForRepeat(tag, i)) {
				continue;
			}
			tagString.append(", ");
			tagString.append(tag.get(i));
		}
		return tagString.toString();
	}

	public void setTag(ArrayList<String> tag) {
		this.tag = tag;
	}

	public ArrayList<String> getRecommendation() {
		return recommendation;
	}

	public String getRecommendationString() {
		StringBuilder recommendationString = new StringBuilder();
		if (recommendation == null || recommendation.size() == 0) {
			return null;
		}
		recommendationString.append(recommendation.get(0));
		for (int i = 1; i < recommendation.size(); i++) {
			if (checkForRepeat(recommendation, i)) {
				continue;
			}
			recommendationString.append(", ");
			recommendationString.append(recommendation.get(i));
		}
		return recommendationString.toString();
	}

	public void setRecommendation(ArrayList<String> recommendation) {
		this.recommendation = recommendation;
	}

	private boolean checkForRepeat(ArrayList<String> list, int i) {
		for (int j = 0; j < i; j++) {
			if (list.get(j).equals(list.get(i))) {
				return true;
			}
		}
		return false;
	}

	public boolean isHasAward() {
		return hasAward;
	}

	public void setHasAward(boolean hasAward) {
		this.hasAward = hasAward;
	}

}
