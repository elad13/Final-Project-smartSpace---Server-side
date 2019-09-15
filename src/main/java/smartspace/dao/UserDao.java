package smartspace.dao;

import java.util.Optional;
import smartspace.data.UserEntity;

public interface UserDao<UserKey> {

	public UserEntity create(UserEntity userEntity);
	public Optional<UserEntity> readById(UserKey userKey);
	public java.util.List<UserEntity> readAll();
	public void update(UserEntity userEntity);
	public void deleteAll();
	
}
