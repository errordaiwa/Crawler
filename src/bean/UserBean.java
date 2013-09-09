package bean;

import java.util.ArrayList;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity
public class UserBean {
	private String userName;
	@Id
	private String userUrl;
	private ArrayList<MovieEvaluate> movieEvaluateList;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	public ArrayList<MovieEvaluate> getMovieEvaluate() {
		return movieEvaluateList;
	}

	public void setMovieEvaluate(ArrayList<MovieEvaluate> movieEvaluateList) {
		this.movieEvaluateList = movieEvaluateList;
	}
}
