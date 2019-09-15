package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactoryImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class MoreUserDaoIntegrationTest2 {

	private EnhancedUserDao<String> userDao;
	private EntityFactoryImpl factory;

	long num = 80;

	@Autowired
	public void setDao(EnhancedUserDao<String> dao) {
		this.userDao = dao;
	}

	@Autowired
	public void setFactory(EntityFactoryImpl factory) {
		this.factory = factory;
	}

	@Before
	public void setup() {
		userDao.deleteAll();
	}

	@After
	public void teardown() {
		userDao.deleteAll();
	}

	/// 1
	@Test
	public void testGenerateUserIdsAreUniqueForTwoUsers() throws Exception {
		// GIVEN the database is clean

		// WHEN I create some user to the database
		int size = 2;

		// @SuppressWarnings("unchecked") /// ? ///
		Set<String> ids = IntStream.range(1, size + 1) // Stream Integer
				.mapToObj(i -> this.factory.createNewUser("user"+String.valueOf(i), "userSmartspace",
						"userName", "avatar", UserRole.PLAYER, num))
				.map(this.userDao::create) // MessageEntity Stream
				.map(UserEntity::getKey) // Long Stream
				.collect(Collectors.toSet());

		// THEN no id is repeated
		assertThat(ids).hasSize(size);
	}

	@Test
	//2
	public void testReadAllWithPagination() throws Exception {

		// GIVEN the database contains 50 Users
		IntStream.range(0, 50) // Stream Integer
				.mapToObj(i -> this.factory.createNewUser("email"+i, "2019B.giron.aptik",
						"String userName", "String avatar", UserRole.PLAYER, num)).forEach(this.userDao::create);

		// WHEN I read 10 Users after skipping first 10
		List<UserEntity> result = this.userDao.readAll(10, 1);

		// THEN I receive 10 results
		assertThat(result).hasSize(10);
	}

	//// 3
	@Test
	public void testReadAllWithPaginationOfSmallerDB() throws Exception {

		// GIVEN the database contains 12 Users
		IntStream.range(0, 12) // Stream Integer
				.mapToObj(i -> this.factory.createNewUser("email" + i, "2019B.giron.aptik",
						"String userName", "String avatar", UserRole.PLAYER, num))
						.forEach(this.userDao::create);


		// WHEN I read 10 Users after skipping first 10
		List<UserEntity> result = this.userDao.readAll(10, 1);

		// THEN I receive 2 results
		assertThat(result).hasSize(2);
	}

	@Test
	//4
	public void testReadAllSortedWithPaginationOfSmallerDB() throws Exception {

		// GIVEN the database contains 12 Users
		List<UserEntity> ids =
		IntStream.range(0, 12) // Stream Integer
				.mapToObj(i -> this.factory.createNewUser("user"+String.valueOf(i), "userSmartspace",
						"userName", "avatar", UserRole.PLAYER, num))
				.map(this.userDao::create) // MessageEntity Stream
				.collect(Collectors.toList());
		
		ids.sort((l1, l2) -> l1.getKey().compareTo(l2.getKey()));

		// WHEN I read 10 Users after skipping first 10 Users ordered by key
		List<UserEntity> result = this.userDao.readAll("key", 10, 1);

		// THEN I receive specific 2 results in specific order
		List<UserEntity> expectedIds = ids.stream().skip(10).collect(Collectors.toList());

		assertThat(result).usingElementComparatorOnFields("key").containsExactlyElementsOf(expectedIds);
	}

//////////////5
	@Test
	public void testGetAllUsersByPatternAndPagination() throws Exception{
		// GIVEN the database contains 12 messages with name containing 'abc'
		// AND the database contains 20 messages that do not have with name containing 'abc'
		IntStream.range(0, 12) // Stream Integer
		.mapToObj(i -> this.factory.createNewUser("email"+String.valueOf(i), "abc",
				"String userName", "String avatar", UserRole.PLAYER, num))
				.forEach(this.userDao::create);

		IntStream.range(12, 33) // Stream Integer
		.mapToObj(i -> this.factory.createNewUser("email"+String.valueOf(i), "xyz",
				"String userName", "String avatar", UserRole.PLAYER, num))
				.forEach(this.userDao::create);

		// WHEN I read 10 messages with name containing 'abc' after skipping first 10 messages
		List<UserEntity> result = this.userDao
				.readUserWithSmartspaceContaining("abc", 10, 1);
		
		// THEN I receive 0 results
		assertThat(result)
			.hasSize(0);
		
	}
}
