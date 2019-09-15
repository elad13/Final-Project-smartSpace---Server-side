package smartspace.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import smartspace.dao.UserDao;
import smartspace.data.UserEntity;

//@Repository
public class UserDaoImpl  implements UserDao<String>  {
	private List<UserEntity> users;
	
	public UserDaoImpl() {
		this.users = Collections.synchronizedList(new ArrayList<>());
	}
	
	protected List<UserEntity> getUsers (){
		return this.users;
	}
	
	@Override
	public UserEntity create(UserEntity ent) {
		ent.setKey(ent.getUserEmail() +"#"+ ent.getUserSmartspace());
		this.users.add(ent);
		return ent;
	}
	
	@Override
	public Optional<UserEntity> readById(String userKey) {
		UserEntity target = null;
		for (UserEntity current : this.users) {
			if (current.getKey().equals(userKey)) {
				target = current;
			}
		}
		if (target != null) {
			return Optional.of(target);
		}else {
			return Optional.empty();
		}	
	}
	
	@Override
	public List<UserEntity> readAll() {
		return this.users;
	}
	
	@Override
	public void update(UserEntity update) {
		synchronized (this.users) {
			UserEntity existing = this.readById(update.getKey())
					.orElseThrow(() -> new RuntimeException("not user to update"));
			if (update.getUserSmartspace() != null) {
				existing.setUserSmartspace(update.getUserSmartspace());
			}
			if (update.getUserEmail() != null) {
				existing.setUserEmail(update.getUserEmail());
			}
			if (update.getUserName() != null) {
				existing.setUserName(update.getUserName());
			}
			if (update.getAvatar() != null) {
				existing.setAvatar(update.getAvatar());
			}
			if (update.getRole() != null) {
				existing.setRole(update.getRole());
			}
			if ((Long)update.getPoints() != null) {
				existing.setPoints(update.getPoints());
			}
		}
	}
	
	@Override
	public void deleteAll() {
		this.users.clear();		
	}
	
	
	
		

}
