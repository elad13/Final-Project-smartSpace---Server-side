package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
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
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactoryImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class MoreActionDaoIntegrationTest2 {

	private EnhancedActionDao actionDao;
	private EntityFactoryImpl factory;

	@Autowired
	public void setDao(EnhancedActionDao dao) {
		this.actionDao = dao;
	}

	@Autowired
	public void setFactory(EntityFactoryImpl factory) {
		this.factory = factory;
	}

	@Before
	public void setup() {
		actionDao.deleteAll();
	}

	@After
	public void teardown() {
		actionDao.deleteAll();
	}

	@Test
	public void testGenerateActionIdsAreUniqueForTwoActions() throws Exception {
		// GIVEN the database is clean

		// WHEN I create some action to the database
		int size = 2;

		
		// @SuppressWarnings("unchecked") /// ? ///
		Set<String> ids = IntStream.range(1, size + 1) // Stream Integer
				.mapToObj(i -> this.factory.createNewAction("elementId",
						"2019B.giron.aptik",
						"actionType",
						new Date(),
						"email@gmail.com",
						"userName",
						null))
				.map(this.actionDao::create) // ActionEntity Stream
				.map(ActionEntity::getKey) // Long Stream
				.collect(Collectors.toSet());

		// THEN no id is repeated
		assertThat(ids).hasSize(size);
	}
	
	@Test
	public void testReadAllWithPagination() throws Exception {
		// GIVEN the database contains 50 Actions
		IntStream.range(0, 50) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("elementId",
				"2019B.giron.aptik",
				"actionType",
				new Date(),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		// WHEN I read 10 Actions after skipping first 10
		List<ActionEntity> result = this.actionDao.readAll(10, 1);

		// THEN I receive 10 results
		assertThat(result).hasSize(10);
	}
	
	@Test
	public void testReadAllWithPaginationOfSmallerDB() throws Exception {
		// GIVEN the database contains 12 Users
		IntStream.range(0, 12) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("elementId",
				"2019B.giron.aptik",
				"actionType",
				new Date(),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		// WHEN I read 10 Actions after skipping first 10
		List<ActionEntity> result = this.actionDao.readAll(10, 1);

		// THEN I receive 2 results
		assertThat(result).hasSize(2);
	}

	@Test
	public void testReadAllSortedWithPaginationOfSmallerDB() throws Exception {
		// GIVEN the database contains 12 Actions
		List<ActionEntity> ids =
				IntStream.range(0, 12) // Stream Integer
				.mapToObj(i -> this.factory.createNewAction("elementId",
						"2019B.giron.aptik",
						"actionType",
						new Date(),
						"email@gmail.com",
						"userName",
						null))
				.map(this.actionDao::create) // ActionEntity Stream
				.collect(Collectors.toList());
		
		ids.sort((l1, l2) -> l1.getKey().compareTo(l2.getKey()));

		// WHEN I read 10 Actions after skipping first 10 Users ordered by key
		List<ActionEntity> result = this.actionDao.readAll("key", 10, 1);

		// THEN I receive specific 2 results in specific order
		List<ActionEntity> expectedIds = ids.stream().skip(10).collect(Collectors.toList());

		assertThat(result).usingElementComparatorOnFields("key").containsExactlyElementsOf(expectedIds);
	}
	
	@Test
	public void testGetAllActionsByPatternAndPagination() throws Exception{
		// GIVEN the database contains 12 Actions with name containing 'abc'
		// AND the database contains 20 Actions that do not have with name containing 'abc'
		IntStream.range(0, 12) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("abc",
				"2019B.giron.aptik",
				"action abc#" + i,
				new Date(),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		IntStream.range(12, 33) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("xyz",
				"2019B.giron.aptik",
				"action xyz#" + i,
				new Date(),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		// WHEN I read 10 Actions with name containing 'abc' after skipping first 10 Actions
		List<ActionEntity> result = this.actionDao
				.readActionWithElementIdContaining("abc", 10, 1);
			
		// THEN I receive 2 results
		assertThat(result).hasSize(2);
	}
	
	@Test
	public void testGetAllActionsByDateAndPagination() throws Exception{
		// GIVEN the database contains 2 Actions with date is yesterday
		// AND the database contains 20 Actions that date is now
		IntStream.range(0, 2) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("elementId",
				"2019B.giron.aptik",
				"action abc#" + i,
				new Date(System.currentTimeMillis() - 24*3600000),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		IntStream.range(2, 23) // Stream Integer
		.mapToObj(i -> this.factory.createNewAction("elementId",
				"2019B.giron.aptik",
				"action xyz#" + i,
				new Date(),
				"email@gmail.com",
				"userName",
				null))
		.forEach(this.actionDao::create);

		// WHEN I read 10 Actions from any time between 2 days ago and one hour ago
		Date twoDaysAgo = new Date(System.currentTimeMillis() - 48*3600000);
		Date oneHourAgo = new Date(System.currentTimeMillis() - 3600000);
		
		List<ActionEntity> result = this.actionDao
				.readActionAvaiable(
						twoDaysAgo, 
						oneHourAgo, 10, 0);
		
		// THEN I receive 2 results
		assertThat(result).hasSize(2);	
	}
}