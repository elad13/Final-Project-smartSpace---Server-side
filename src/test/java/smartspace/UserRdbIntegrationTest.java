package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.rdb.RdbUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class UserRdbIntegrationTest {
	@Autowired
	private RdbUserDao userRep;
	@Autowired
	private EntityFactory entityFactory;
	
	private UserEntity testUser;
	
	@Before
	public void addTestUser() {
		testUser = new UserEntity();
		
		testUser = entityFactory.createNewUser("email@gmail.com",
				"2019B.giron.aptik",
				"userName",
				"avatar.png",
				UserRole.PLAYER,
				0l);
		userRep.create(testUser);
	}

	@After
	public void cleanUp() {
		userRep.deleteAll();
	}
	
	@Test
	public void addUser() {
		List<UserEntity> users = userRep.readAll();
		assertThat(users.get(1).getKey()).isEqualTo("manager" + "#" + "2019B.giron.aptik");
	}
	
	@Test
	public void readById() {
		String expectedKey = testUser.getUserEmail() + "#" + testUser.getUserSmartspace();
		Optional<UserEntity> userRead = userRep.readById(expectedKey);
		assert(userRead.isPresent());
	}
	
	@Test
	public void readByWrongId() {
		Optional<UserEntity> userRead = userRep.readById(testUser.getUserEmail() + "#" + testUser.getUserSmartspace());
		assert(userRead.isPresent());
	}
	
	@Test
	public void readAll() {
		UserEntity userToCreate2 = entityFactory.createNewUser("email2@gmail.com",
				"2019B.giron.aptik",
				"userName2",
				"avatar2.png",
				UserRole.PLAYER,
				0);
		
		userRep.create(userToCreate2);

		List<UserEntity> users = userRep.readAll();
		
		assertThat(users.size()).isEqualTo(2);
		
		UserEntity firstUser = users.get(0);
		UserEntity secondUser = users.get(1);
		
		checkUsersEquals(firstUser, testUser);
		checkUsersEquals(secondUser, userToCreate2);
	}
	
	@Test
	public void updateSome() {
		testUser.setAvatar(null);
		testUser.setRole(UserRole.MANAGER);
		
		userRep.update(testUser);
		
		List<UserEntity> users = userRep.readAll();
		
		assertThat(users.size()).isEqualTo(1);
		
		// Checking that the avatar was not changed
		assertThat(users.get(0).getAvatar()).isEqualTo("avatar.png");
		assertThat(users.get(0).getRole()).isEqualTo(testUser.getRole());
		assertThat(users.get(0).getPoints()).isEqualTo(testUser.getPoints());
		assertThat(users.get(0).getUserEmail()).isEqualTo(testUser.getUserEmail());
		assertThat(users.get(0).getUserName()).isEqualTo(testUser.getUserName());
	}
	
	@Test
	public void updatePointsOnly() {
		testUser.setPoints(4);
		testUser.setAvatar(null);
		testUser.setRole(null);
		
		userRep.update(testUser);
		
		List<UserEntity> users = userRep.readAll();
		
		//assertThat(users.size()).isEqualTo(1);
		
		UserEntity temp = users.get(users.size()-1);
		
		// Checking that the avatar was not changed
		assertThat(temp.getAvatar()).isEqualTo("avatar.png");
		assertThat(temp.getRole()).isEqualTo(UserRole.PLAYER);
		assertThat(temp.getPoints()).isEqualTo(testUser.getPoints());
		assertThat(temp.getUserEmail()).isEqualTo("email@gmail.com");
		assertThat(temp.getUserName()).isEqualTo("userName");
	}
	
	
	@Test
	public void update() {
		List<UserEntity> users1 = userRep.readAll();
		System.err.println(users1.size());
		System.err.println(users1);
		testUser.setAvatar("Avatar2.png");
		testUser.setPoints(1);
		testUser.setRole(UserRole.MANAGER);
		testUser.setUserName("user");
		
		userRep.update(testUser);
		
		List<UserEntity> users = userRep.readAll();
		System.err.println(users.size());
		System.err.println(users);
		
		//assertThat(users.size()).isEqualTo(2);
		
		checkUsersEquals(users.get(users.size()-1), testUser);
	}
	
	private void checkUsersEquals(UserEntity user1, UserEntity user2) {
		assertThat(user1.getKey()).isEqualTo(user2.getUserEmail() + "#" + user2.getUserSmartspace());
		assertThat(user1.getUserEmail()).isEqualTo(user2.getUserEmail());
		assertThat(user1.getUserName()).isEqualTo(user2.getUserName());
		assertThat(user1.getAvatar()).isEqualTo(user2.getAvatar());
		assertThat(user1.getRole()).isEqualTo(user2.getRole());
		assertThat(user1.getPoints()).isEqualTo(user2.getPoints());
	}
}
