package smartspace;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import smartspace.dao.EnhancedElementDao;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.ElementEntity;
import smartspace.layout.BoundaryEmailKey;
import smartspace.layout.BoundaryIdKey;
import smartspace.layout.ElementBoundary;
import smartspace.layout.LocationBoundary;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class ElementControllerIntegrationTests {
	private String baseUrl;
	private int port;
	private RestTemplate restTemplate;
	private EnhancedElementDao<String> elementDao;
	
	@Autowired
	public void setElementDao(EnhancedElementDao<String> elementDao) {
		this.elementDao = elementDao;
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
		this.elementDao
			.deleteAll();
	}
	
	@Test
	public void testPostNewElementAsManagerWithNullKey() throws Exception{
		// GIVEN the database is empty
		
		// WHEN I POST new element
		Map<String, Object> details = new HashMap<>();
		details.put("y", 10.0);
		details.put("x", "10");
		ElementBoundary newElement = new ElementBoundary();
		newElement.setKey(new BoundaryIdKey(null, null));
		newElement.setName("Demo1");
		newElement.setCreator(new BoundaryEmailKey("manager","2019B.giron.aptik"));
		newElement.setCreated(new Date());
		newElement.setElementProperties(details);
		newElement.setLatlng(new LocationBoundary(40, 50));
		this.restTemplate
			.postForObject(
					this.baseUrl + "/smartspace/elements/{managerSmartspace}/{managerEmail}", 
					newElement, 
					ElementBoundary.class, 
					"2019B.giron.aptik", "manager");
		
		// THEN the database contains a single element
		assertThat(this.elementDao
			.readAll())
			.hasSize(1);
	}
	
	@Test(expected=Exception.class)
	public void testPostNewElementWithBadKey() throws Exception{
		// GIVEN the database is empty
		
		// WHEN I POST new element with bad code
		Map<String, Object> details = new HashMap<>();
		details.put("y", 10.0);
		details.put("x", "10");
		ElementBoundary newElement = new ElementBoundary();
		newElement.setName("Demo1");
		newElement.setCreator(new BoundaryEmailKey("giron","2019B.giron.aptik"));
		newElement.setCreated(new Date());
		newElement.setKey(new BoundaryIdKey(null, null));
		newElement.setLatlng(new LocationBoundary(7, 8));
		newElement.setElementProperties(details);
		this.restTemplate
			.postForObject(
					this.baseUrl + "/smartspace/elements/{managerSmartspace}/{managerEmail}", 
					newElement, 
					ElementBoundary.class, 
					"2019B.giron.aptik", "giron");
		
		// THEN the test end with exception
	}

	@Test
	public void testGetAllElementsUsingPaginationAsUser() throws Exception{
		// GIVEN the database contains 3 elements
		int size = 3;
		IntStream.range(1, size + 1)
			.mapToObj(i->new ElementEntity("String name" + i, "String type2",
					new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail2", "String creatorSmartspace2",  
					true, null))
			.forEach(this.elementDao::create);
		
		// WHEN I GET elements of size 10 and page 0
		ElementBoundary[] response = 
		this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/elements/{userSmartspace}/{userEmail}?size={size}&page={page}", 
					ElementBoundary[].class, 
					"2019B.giron.aptik", "manager", 10, 0);
		
		// THEN I receive 3 elements
		assertThat(response)
			.hasSize(size);
	}
	
	
	@Test
	public void testUpdateElement() throws Exception{
		// GIVEN the database contains a element
		ElementEntity testElementEntity = new ElementEntity("String test", "String type2",
				new Location(3, 2), new Date(System.currentTimeMillis()), "manager", "2019B.giron.aptik",  
				true, null);
		testElementEntity.setElementId(null);
		testElementEntity.setElementSmartspace(null);
		testElementEntity.setMoreAttributes(new HashMap<>());
		testElementEntity = this.elementDao
			.create(testElementEntity);
		
		// WHEN I update the element details
		Map<String, Object> newDetails = new HashMap<>();
		newDetails.put("x", 10.0);
		newDetails.put("y", "new details");
		newDetails.put("expired", true);
		ElementBoundary testElementBoundary = new ElementBoundary();
		testElementBoundary.setCreator(new BoundaryEmailKey("2019B.giron.aptik", "giron.test"));
		testElementBoundary.setKey(new BoundaryIdKey(null, null));
		testElementBoundary.setElementProperties(newDetails);
		testElementBoundary.setLatlng(new LocationBoundary(50, 30));
		this.restTemplate
			.put(this.baseUrl + "/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}", 
					testElementBoundary, 
					"2019B.giron.aptik", "manager", "2019B.giron.aptik", testElementEntity.getElementId());
		
		// THEN the database contains updated details
		assertThat(this.elementDao.readById(testElementEntity.getKey()))
			.isNotNull()
			.isPresent()
			.get()
			.extracting("key", "moreAttributes")
			.containsExactly(testElementEntity.getKey(), newDetails);
		
	}
	
	@Test
	public void testGetAllElementsUsingPaginationOfSecondNonExistingPage() throws Exception{
		// GIVEN the database contains 10 elements
		IntStream
			.range(0, 10)
			.forEach(i->this.elementDao.create(new ElementEntity("String name" + i, "String type2",
					new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail2", "String creatorSmartspace2",  
					true, null)));
		
		// WHEN I GET elements of size 10 and page 1
		String[] result = 
		  this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/elements/{managerSmartspace}/{managerEmail}?size={size}&page={page}", 
					String[].class, 
					"2019B.giron.aptik","manager", 10, 1);
		
		// THEN the result is empty
		assertThat(result)
			.isEmpty();
		
	}
	
	@Test
	public void testGetAllElementsUsingPaginationAsAdmin() throws Exception{
		// GIVEN the database contains 3 elements
		int size = 3;
		IntStream.range(1, size + 1)
			.mapToObj(i->new ElementEntity("String name" + i, "String type2",
					new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail2", "String creatorSmartspace2",  
					true, null))
			.peek(i-> i.setKey(i+"#2019B.test"))
			.forEach(this.elementDao::insert);
		
		// WHEN I GET elements of size 10 and page 0
		ElementBoundary[] response = 
		this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}?size={size}&page={page}", 
					ElementBoundary[].class, 
					"2019B.test", "admin", 10, 0);
		
		// THEN I receive 3 elements
		assertThat(response)
			.hasSize(size);
	}
	
	@Test
	public void testDeleteByKey() throws Exception {
		// GIVEN the database contains a single message
		String key = this.elementDao
				.create(new ElementEntity("String test", "String type2",
						new Location(3, 2), new Date(System.currentTimeMillis()), "manager", "2019B.giron.aptik",  
						true, null))
				.getKey();
		// System.err.println("***** " + this.elementDao.readAll().size() + " ******");
		// WHEN I delete using the message key
		this.restTemplate.delete(this.baseUrl + "/smartspace/elements/delete/{key}", key);

		// THEN the database is empty
		assertThat(this.elementDao.readAll()).hasSize(0);

	}

	@Test
	public void testDeleteByKeyWhileDatabseIsNotEmptyAtTheEnd() throws Exception {
		// GIVEN the database contains 2 element
		ElementEntity check = this.elementDao
				.create(new ElementEntity("String test", "String type2",
						new Location(3, 2), new Date(System.currentTimeMillis()), "manager", "2019B.giron.aptik",  
						true, null));
		String key = check.getKey();
		
		this.elementDao
		.create(new ElementEntity("String test1", "String type23",
				new Location(3, 2), new Date(System.currentTimeMillis()), "manager", "2019B.giron.aptik",  
				true, null));
		
		 //System.err.println("***** " + this.elementDao.readAll().size() + "\n" + this.elementDao.readAll() + " ******");

		

		// WHEN I delete the 1st message using the element key
		this.restTemplate.delete(this.baseUrl + "/smartspace/elements/delete/{key}", key);

		// THEN the database contains 1 element
		// AND the database does not contain the deleted element
		assertThat(this.elementDao.readAll()).hasSize(1).usingElementComparatorOnFields("key").doesNotContain(check);
	}

	

}

