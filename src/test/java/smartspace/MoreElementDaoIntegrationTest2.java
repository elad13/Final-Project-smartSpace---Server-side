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
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactoryImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class MoreElementDaoIntegrationTest2 {
	private EnhancedElementDao<String> elementDao;
	private EntityFactoryImpl factory;
	
	long num = 80;
	
		@Autowired
	public void setDao(EnhancedElementDao<String> dao) {
		this.elementDao = dao;
	}

	@Autowired
	public void setFactory(EntityFactoryImpl factory) {
		this.factory = factory;
	}

	@Before
	public void setup() {
		elementDao.deleteAll();
	}

	@After
	public void teardown() {
		elementDao.deleteAll();
	}
	
	/// 1
		@Test
		public void testGenerateElementIdsAreUniqueForTwoElements() throws Exception {
			// GIVEN: the database is clean

			// WHEN: I create some Elements to the database
			int size = 2;

			Set<String> ids = IntStream.range(1, size + 1) // Stream Integer
					.mapToObj(i -> this.factory.createNewElement("name"+String.valueOf(i), "type",
							new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null))
					.map(this.elementDao::create) 
					.map(ElementEntity::getKey) // Long Stream
					.collect(Collectors.toSet());

			// THEN: no id is repeated
			assertThat(ids).hasSize(size);
			
		}
		
		
		@Test
		//2
		public void testReadAllWithPagination() throws Exception {

			// GIVEN the database contains 50 Elements
			IntStream.range(0, 50) // Stream Integer
					.mapToObj(i -> this.factory.createNewElement("name"+String.valueOf(i), "type",
							new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null)).forEach(this.elementDao::create);

			// WHEN I read 10 Elements after skipping first 10
			List<ElementEntity> Elementresult = this.elementDao.readAll(10, 1);

			// THEN I receive 10 results
			assertThat(Elementresult).hasSize(10);
		}
			
		
	//// 3
		@Test
		public void testReadAllWithPaginationOfSmallerDB() throws Exception {

			// GIVEN the database contains 12 Elements
			IntStream.range(0, 12) // Stream Integer
					.mapToObj(i -> this.factory.createNewElement("name"+String.valueOf(i), "type",
							new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null))
							.forEach(this.elementDao::create);


			// WHEN I read 10 Elements after skipping first 10
			List<ElementEntity> Elementresult = this.elementDao.readAll(10, 1);

			// THEN I receive 2 results
			assertThat(Elementresult).hasSize(2);
		}	
		
		@Test
		///4
		public void testReadAllSortedWithPaginationOfSmallerDB() throws Exception {

			// GIVEN the database contains 12 Elements
			List<ElementEntity> ids =
			IntStream.range(0, 12) // Stream Integer
					.mapToObj(i -> this.factory.createNewElement("name"+String.valueOf(i), "type",
							new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null))
					.map(this.elementDao::create) 
					.collect(Collectors.toList());
			
			ids.sort((l1, l2) -> l1.getKey().compareTo(l2.getKey()));

			// WHEN I read 10 Elements after skipping first 10 Elements ordered by key
			List<ElementEntity> result = this.elementDao.readAll("key", 10, 1);

			// THEN I receive specific 2 results in specific order
			List<ElementEntity> expectedIds = ids.stream().skip(10).collect(Collectors.toList());
			
			assertThat(result).usingElementComparatorOnFields("key").containsExactlyElementsOf(expectedIds);
		}
		
////5
@Test
public void testGetAllElementsByPatternAndPagination() throws Exception{
	// GIVEN the database contains 12 elements with name containing 'abc'
	// AND the database contains 20 elements that do not have with name containing 'abc'
	IntStream.range(0, 12) // Stream Integer
	.mapToObj(i -> this.factory.createNewElement("abc"+String.valueOf(i), "Type",
			new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null))
			.forEach(this.elementDao::create);

	IntStream.range(12, 33) // Stream Integer
	.mapToObj(i -> this.factory.createNewElement("xyz"+String.valueOf(i), "xyz",
			new Location(3,4), new Date(), "testo@mysmartspace.me", "mysmartspace", false, null))
			.forEach(this.elementDao::create);

	// WHEN I read 10 Elements with name containing 'abc' after skipping first 10 elements
	List<ElementEntity> result = this.elementDao.readElementWithNameContaining("abc", 10, 1);
	
	// THEN I receive 2 results
	assertThat(result)
		.hasSize(2);
	
}
	
		
}
