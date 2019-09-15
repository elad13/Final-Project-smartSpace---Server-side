package smartspace.infra;

import java.util.Collection;
import java.util.List;
import smartspace.data.UserEntity;

public interface UserService {
	
	public UserEntity newUser(UserEntity entity);
	public List<UserEntity> getUserUsingPagination (String adminSmartspace, String adminEmail, int size, int page);
	public Collection<UserEntity> store(String adminSmartspace, String adminEmail, Collection<UserEntity> actionEntitiesToImport);
	public UserEntity login(String email, String smartspace);
	public UserEntity getUserById(String email, String smartspace);
	public void update(String userKey, UserEntity user);
	public void deleteByKey(String key);
	public void updateWithoutPoints(String userKey, UserEntity user);

}
