package bean;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity
public class PeopleBean {
	@Id
	private String peopleUrl;
	private String sex;
	private String birthday;
	private String birthplace;
	private String career;
	private String peopleEName;
	private String peopleCName;
	private String peopleName;
	private boolean hasAward;
	
	public String getPeopleUrl() {
		return peopleUrl;
	}
	public void setPeopleUrl(String peopleUrl) {
		this.peopleUrl = peopleUrl;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getBirthplace() {
		return birthplace;
	}
	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}
	public String getCareer() {
		return career;
	}
	public void setCareer(String career) {
		this.career = career;
	}
	public String getPeopleEName() {
		return peopleEName;
	}
	public void setPeopleEName(String peopleEName) {
		this.peopleEName = peopleEName;
	}
	public String getPeopleCName() {
		return peopleCName;
	}
	public void setPeopleCName(String peopleCName) {
		this.peopleCName = peopleCName;
	}
	public String getPeopleName() {
		return peopleName;
	}
	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}
	public boolean isHasAward() {
		return hasAward;
	}
	public void setHasAward(boolean hasAward) {
		this.hasAward = hasAward;
	}
}
