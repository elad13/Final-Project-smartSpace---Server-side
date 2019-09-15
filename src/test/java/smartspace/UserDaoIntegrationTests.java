package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactoryImpl;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class UserDaoIntegrationTests {
	private UserDao<String> dao;
	private EntityFactoryImpl factory;

	@Autowired
	public void setDao(UserDao<String> dao) {
		this.dao = dao;
	}

	@Autowired
	public void setFactory(EntityFactoryImpl factory) {
		this.factory = factory;
	}

	@Before
	public void setup() {
		dao.deleteAll();
	}

	@After
	public void teardown() {
		dao.deleteAll();
	}

	@Test(expected = Exception.class)
	public void testCreateWithNullUser() throws Exception {
		// GIVEN nothing

		// WHEN I create null user
		this.dao.create(null);
		
		// THEN create method throws exeption
	}

	@Test
	public void testCreateGetByIdWithValidUser() throws Throwable {
		// GIVEN dao is initialized and clean
		long num = 80;
		Map<String, Object> details = new HashMap<>();
		details.put("test", new Boolean(true));
		//Map<String, Object> moreAttributes;
		
		UserEntity user = this.factory.createNewUser("String userEmail", "String userSmartspace",
		"String userName", "String avatar", UserRole.PLAYER, num);

		UserEntity userInDB = this.dao.create(user);

		UserEntity userFromDB = (UserEntity) this.dao.readById(userInDB.getKey())
				.orElseThrow(() -> new RuntimeException("could not find user by key"));

		// THEN the same user is returned
		assertThat(userFromDB).isNotNull().extracting("userEmail", "userSmartspace","userName")
		.containsExactly("String userEmail", "2019B.giron.aptik", "String userName");

	}

	@Test
	public void testCreateUpdateReadByIdDeleteAllReadAll() throws Throwable {
		// GIVEN nothing

		// WHEN I create a new user
		// AND update user
		// AND get user by key
		// AND Delete all users
		// AND read all
		long num1 = 80;
		long num2 = 40;
		Map<String, Object> details = new HashMap<>();
		details.put("title", "hello");
		details.put("signature", 2123154546416841684L);
		details.put("langauge", "EN");
		details.put("interesting", false);
		UserEntity user1 = this.factory.createNewUser("String userSmartspace1", "userEmail1",
				"String userName1", "String avatar", UserRole.PLAYER, num1);
		user1 = this.dao.create(user1);

		
		  UserEntity update = this.factory.createNewUser("String userSmartspace2", "userEmail2",
					"String userName2", "String avatar2", UserRole.MANAGER, num2);
		  update.setKey(user1.getKey());
		  this.dao.update(update);
		 
		this.dao.readById(user1.getKey())
		.orElseThrow(() -> new RuntimeException("not user after update"));

		this.dao.deleteAll();

		List<UserEntity> list = this.dao.readAll();

		// THEN the created user received an id > 0
		// AND the dao contains nothing
		assertThat(user1.getKey()).isNotNull();
		assertThat(list).isEmpty();

	}
}
