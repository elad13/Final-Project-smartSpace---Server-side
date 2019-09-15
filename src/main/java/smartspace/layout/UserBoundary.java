package smartspace.layout;

import smartspace.AppProperties;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;


public class UserBoundary {

	private BoundaryEmailKey key;
	private String username;
	private String avatar;
	private UserRole role;
	private long points;
	private AppProperties appProperties;

	public UserBoundary() {
	}

	public UserBoundary(UserEntity entity) {
		if(entity.getKey() != null) {
			String ukey = entity.getKey();
			String[] tmpArr = ukey.split("#");
			this.key = new BoundaryEmailKey(tmpArr[0], tmpArr[1]);
		}
		else {
			throw new NullPointerException("Null key");
		}
		this.username = entity.getUserName();
		this.avatar = entity.getAvatar();
		this.role = entity.getRole();
		this.points = entity.getPoints();
	}
	
	public UserBoundary(NewUserForm userForm) {
		if (userForm != null) {
			if (userForm.getEmail() != null) {
				this.key = new BoundaryEmailKey(userForm.getEmail(), appProperties.getName());
			} else {
				throw new NullPointerException("Null email");
			}
			this.username = userForm.getUsername();
			this.avatar = userForm.getAvatar();
			this.role = userForm.getRole();
			this.points = 0;
		}
		else {
			throw new NullPointerException("Null Form");
		}
	}

	public BoundaryEmailKey getKey() {
		return key;
	}

	public void setKey(BoundaryEmailKey key) {
		this.key = key;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

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

	public UserEntity convertToEntity(UserBoundary user) {
		UserEntity entity = new UserEntity();
		if (user.getKey() != null) {
			entity.setUserName(user.getUsername());
			entity.setAvatar(user.getAvatar());
			entity.setRole(user.getRole());
			entity.setPoints(user.getPoints());
			entity.setKey(user.getKey().toString());
		} else {
			throw new NullPointerException("Null key");
		}
		return entity;
	}

//	@Override
//	public String toString() {
//		return "UserTO [email=" + userEmail + ", smartspace=" + userSmartspace + ", username=" + userName + ", avatar=" + avatar
//				+ ", role=" + role + ", points=" + points + "]";
//	}

}
