package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.BoundaryEmailKey;
import smartspace.layout.NewUserForm;
import smartspace.layout.UserBoundary;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class UserControllerIntegrationTests {
	private String baseUrl;
	private int port;
	private RestTemplate restTemplate;
	private EnhancedUserDao<String> userDao;

	
	@Autowired
	public void setUserDao(EnhancedUserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
		this.restTemplate = new RestTemplate();
	}
	
	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port;
	}
	
	@After
	public void tearDown() {
		this.userDao
			.deleteAll();
	}
	
	@Test
	public void testPostCreateNewUserWithNewUserForm() throws Exception{
		// GIVEN the database is empty
		
		// WHEN I POST new user
		NewUserForm newUserForm = new NewUserForm();
		newUserForm.setAvatar(":)");
		newUserForm.setEmail("test");
		newUserForm.setRole(UserRole.PLAYER);
		newUserForm.setUsername("Mr.Test");
		this.restTemplate
			.postForObject(
					this.baseUrl + "/smartspace/users", 
					newUserForm, 
					UserBoundary.class);
		
		// THEN the database contains a single user
		assertThat(this.userDao
			.readAll())
			.hasSize(1);
	}
	
	@Test
	public void testLoginValidUser() throws Exception{
		// GIVEN the database contains 3 users
		UserEntity testUser = new UserEntity("test@test.com", "2019B.giron.aptik", "giron", ":)", UserRole.PLAYER, 40);
		this.userDao.create(testUser);

		
		// WHEN I GET users of size 10 and page 0
		UserBoundary response = 
		this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/users/login/{userSmartspace}/{userEmail}", 
					UserBoundary.class, 
					"2019B.giron.aptik", "test@test.com", 10, 0);
		
		// THEN I receive 3 users
		assertThat(response.convertToEntity(response)).isEqualToComparingFieldByField(testUser);
	}
	@Test
	public void testGetAllUsersUsingPaginationAsAdmin() throws Exception{
		UserEntity admin = new UserEntity();
		admin.setRole(UserRole.ADMIN);
		admin.setKey("admin#2019B.test");
		admin.setAvatar(":)");
		admin.setPoints(122);
		admin.setUserName("Giron");
		this.userDao.insert(admin);
		// GIVEN the database contains 3 users
		int size = 3;
		IntStream.range(1, size + 1)
			.mapToObj(i->new UserEntity("email@gmail.com"+i,
					"2019B.test",
					"userName"+i,
					"avatar.png",
					UserRole.PLAYER,
					0l))
			.peek(i-> i.setKey(i+"#2019B.test"))
			.forEach(this.userDao::insert);
		
		// WHEN I GET users of size 10 and page 0
		UserBoundary[] response = 
		this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/admin/users/{adminSmartspace}/{adminEmail}?size={size}&page={page}", 
					UserBoundary[].class, 
					"2019B.test", "admin", 10, 0);
		
		// THEN I receive 3 users +1 Admin
		assertThat(response)
			.hasSize(size+1);
	}
	
	@Test
	public void testUpdateUserWithoutTheirPoints() throws Exception{
		// GIVEN the database contains a user
		UserEntity testUserEntity = new UserEntity("test@test.com", "2019B.giron.aptik", "giron", ":)", UserRole.PLAYER, 40);
		this.userDao.create(testUserEntity);
		
		
		UserBoundary testUserBoundary = new UserBoundary();
		testUserBoundary.setAvatar(":)");
		testUserBoundary.setKey(new BoundaryEmailKey(null,null));
		testUserBoundary.setPoints(49);
		testUserBoundary.setRole(UserRole.PLAYER);
		testUserBoundary.setUsername("Mr.Test");

		this.restTemplate
			.put(this.baseUrl + "/smartspace/users/login/{userSmartspace}/{userEmail}", 
					testUserBoundary, 
					"2019B.giron.aptik", "test@test.com");
		
		// THEN the database contains updated details
		assertThat(this.userDao.readById(testUserEntity.getKey()))
			.isNotNull()
			.isPresent()
			.get()
			.extracting("key")
			.containsExactly(testUserEntity.getKey());
		
	}
	
	@Test
	public void testDeleteByKey() throws Exception {
		// GIVEN the database contains a single user
		String key = this.userDao
				.create(new UserEntity("test@test.com", "2019B.giron.aptik", "giron", ":)", UserRole.PLAYER, 40))
				.getKey();
		// System.err.println("***** " + this.userDao.readAll().size() + " ******");
		// WHEN I delete using the user key
		this.restTemplate.delete(this.baseUrl + "/smartspace/users/delete/{key}", key);

		// THEN the database is empty
		assertThat(this.userDao.readAll()).hasSize(0);
	}

	
	@Test
	public void testDeleteByKeyWhileDatabseIsNotEmptyAtTheEnd() throws Exception {
		UserEntity check = this.userDao
				.create(new UserEntity("test@test.com", "2019B.giron.aptik", "giron", ":)", UserRole.PLAYER, 40));
		String key = check.getKey();
		
		this.userDao
		.create(new UserEntity("test@test.com123", "2019B.giron.aptik123", "giron123", ":)", UserRole.PLAYER, 40));
		
		 System.err.println("***** " + this.userDao.readAll().size() + "\n" + this.userDao.readAll() + " ******");

		

		// WHEN I delete the 3rd user using the user key
		this.restTemplate.delete(this.baseUrl + "/smartspace/users/delete/{key}", key);

		// AND the database does not contain the deleted user
		assertThat(this.userDao.readAll()).hasSize(2).usingElementComparatorOnFields("key").doesNotContain(check);
	}

}
