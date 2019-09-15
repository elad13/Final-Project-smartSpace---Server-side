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
import smartspace.dao.rdb.RdbActionDao;
import smartspace.dao.rdb.RdbElementDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.layout.ActionBoundary;
import smartspace.layout.BoundaryEmailKey;
import smartspace.layout.BoundaryIdKey;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class ActionControllerIntegrationTests {
	
	private String baseUrl;
	private int port;
	private RestTemplate restTemplate;
	private RdbActionDao actionDao;
	private RdbElementDao elementDao;

	@Autowired
	public void setActinoDao(RdbActionDao actionDao) {
		this.actionDao = actionDao;
	}
	
	@Autowired
	public void setElementDao(RdbElementDao elementDao) {
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
	
	@PostConstruct
	public void initElement() {
		ElementEntity elementTest = new ElementEntity("String test", "String type2",
				new Location(3, 2), new Date(System.currentTimeMillis()), "manager", "2019B.giron.aptik",  
				true, null);
		elementTest.setElementId(null);
		elementTest.setElementSmartspace(null);
		elementTest.setMoreAttributes(new HashMap<>());
		elementTest = this.elementDao.create(elementTest);
	}
	
	@After
	public void tearDown() {
		this.actionDao
			.deleteAll();
	}
	
	@Test
	public void testGetAllActionsUsingPaginationAsAdmin() throws Exception{
		// GIVEN the database contains 3 actions
		int size = 3;
		IntStream.range(1, size + 1)
			.mapToObj(i->new ActionEntity("elementId2",
					"2019B.giron.aptik",
					"actionType2",
					new Date(),
					"email@gmail.com2",
					"userName2",null))
			.forEach(this.actionDao::create);
		
		// WHEN I GET actions of size 10 and page 0
		ActionBoundary[] response = 
		this.restTemplate
			.getForObject(
					this.baseUrl + "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}?size={size}&page={page}", 
					ActionBoundary[].class, 
					"2019B.test", "admin", 10, 0);
		
		// THEN I receive 3 actions
		assertThat(response)
			.hasSize(size);
	}

	
	@Test
	public void testInvokeAction() throws Exception{
		// GIVEN the database is empty
		// WHEN I POST new action
		Map<String, Object> details = new HashMap<>();
		details.put("y", 10.0);
		details.put("x", "10");
		ActionBoundary newAction = new ActionBoundary();
		newAction.setActionKey(new BoundaryIdKey(null, null));
		newAction.setPlayer(new BoundaryEmailKey("manager","2019B.giron.aptik"));
		newAction.setCreated(new Date());
		newAction.setProperties(details);
		newAction.setType("echo");
		newAction.setElement(new BoundaryIdKey("1", "2019B.giron.aptik"));
		this.restTemplate
			.postForObject(
					this.baseUrl + "/smartspace/actions", 
					newAction, 
					ActionBoundary.class);
		
		// THEN the database contains a single action
		assertThat(this.actionDao
			.readAll())
			.hasSize(1);
	}
	
	@Test
	public void testDeleteByKey() throws Exception {
		// GIVEN the database contains a single message
		String key = this.actionDao
				.create(new ActionEntity("elementId2",
						"2019B.giron.aptik",
						"actionType2",
						new Date(),
						"email@gmail.com2",
						"userName2",null))
				.getKey();
		// System.err.println("***** " + this.actionDao.readAll().size() + " ******");
		// WHEN I delete using the message key
		this.restTemplate.delete(this.baseUrl + "/smartspace/actions/delete/{key}", key);

		// THEN the database is empty
		assertThat(this.actionDao.readAll()).hasSize(0);

	}

	@Test
	public void testDeleteByKeyWhileDatabseIsNotEmptyAtTheEnd() throws Exception {
		// GIVEN the database contains actions
		ActionEntity check = this.actionDao
				.create(new ActionEntity("elementId2",
						"2019B.giron.aptik",
						"actionType2",
						new Date(),
						"email@gmail.com2",
						"userName2",null));
		String key = check.getKey();
		
		this.actionDao
		.create(new ActionEntity("elementId23",
				"2019B.giron.aptik",
				"actionType2",
				new Date(),
				"email@gmail.com2",
				"userName2",null));
		
		 //System.err.println("***** " + this.actionDao.readAll().size() + "\n" + this.actionDao.readAll() + " ******");

		

		// WHEN I delete the 3rd message using the action key
		this.restTemplate.delete(this.baseUrl + "/smartspace/actions/delete/{key}", key);

		// THEN the database contains actions
		// AND the database does not contain the deleted message
		assertThat(this.actionDao.readAll()).hasSize(1).usingElementComparatorOnFields("key").doesNotContain(check);
	}
	
}
