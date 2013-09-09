package bean;

import com.github.jmkgreen.morphia.annotations.Embedded;

@Embedded
public class People {
	private String name;
	private String url;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof People && ((People) obj).getUrl().equals(url))
			return true;
		else
			return false;
	}
}
