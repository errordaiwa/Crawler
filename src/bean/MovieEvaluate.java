package bean;

import com.github.jmkgreen.morphia.annotations.Embedded;

@Embedded
public class MovieEvaluate {
	private String url;
	private String rating;
	private String ratingTime;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getRatingTime() {
		return ratingTime;
	}
	public void setRatingTime(String ratingTime) {
		this.ratingTime = ratingTime;
	}
}
