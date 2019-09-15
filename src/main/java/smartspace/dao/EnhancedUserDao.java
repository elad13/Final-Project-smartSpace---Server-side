package smartspace.dao;

import java.util.List;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public interface EnhancedUserDao<Key> extends UserDao<Key>{
	public List<UserEntity> readAll(int size, int page);
	public List<UserEntity> readAll(String sortBy, int size, int page);
	public List<UserEntity> readUserWithSmartspaceContaining (String text, int size, int page);
	public UserEntity updateOrInsert (UserEntity user);
	public UserEntity insert (UserEntity user);
	public UserRole getUserRole(String adminSmartspace, String adminEmail);
	public void deleteById(String key);
	public void updateWithoutPoints(UserEntity userToUpdate);
}
