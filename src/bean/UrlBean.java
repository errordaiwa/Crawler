package bean;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity
public class UrlBean {
	@Id
	private String url;
	private boolean needToSavePeople;
	private boolean needToSaveUser;
	private String type;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isNeedToSavePeople() {
		return needToSavePeople;
	}
	public void setNeedToSavePeople(boolean needToSavePeople) {
		this.needToSavePeople = needToSavePeople;
	}
	public boolean isNeedToSaveUser() {
		return needToSaveUser;
	}
	public void setNeedToSaveUser(boolean needToSaveUser) {
		this.needToSaveUser = needToSaveUser;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
