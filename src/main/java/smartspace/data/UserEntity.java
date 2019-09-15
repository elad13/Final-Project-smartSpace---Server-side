package smartspace.data;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;



//@Entity
//@Table(name = "USER")
@Document(collection="USERS")
public class UserEntity implements SmartSpaceEntity<String> {
	
	private String userSmartspace;
	private String userEmail;
	private String userName;
	private String avatar;
	private UserRole role;
	private long points;
	private String key;
	
	public UserEntity() {
		
	}
	
	public UserEntity(String userEmail, String userSmartspace, String userName, String avatar,
			UserRole role, long points){
		super();
		this.userSmartspace = userSmartspace;
		this.userEmail = userEmail;
		this.userName = userName;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}
	
	public String getUserSmartspace() {
		return userSmartspace;
	}
	
	public void setUserSmartspace(String userSmartspace) {
		this.userSmartspace = userSmartspace;
	}

	//@Transient
	@JsonIgnore
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}
	public void setPoints(long points) {
		this.points = points;
	}
	
	@Override
	//@Id
	//@Column(name="ID")
	@org.springframework.data.annotation.Id
	public String getKey() {
		return this.key;
		//return this.userEmail + "#" + this.userSmartspace;
	}

	@Override
	public void setKey(String key) {
		String[] tmpArr = key.split("#");
		this.userEmail = tmpArr[0];
		this.userSmartspace = tmpArr[1];
		this.key = key;
	}

	@Override
	public String toString() {
		return "UserEntity [userSmartspace=" + userSmartspace + ", userEmail=" + userEmail + ", username=" + userName
				+ ", avatar=" + avatar + ", role=" + role + ", points=" + points + "]";
	}
	
}