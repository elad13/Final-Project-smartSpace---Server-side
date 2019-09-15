package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.rdb.RdbElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class ElementRdbIntegrationTest {
	@Autowired
	private RdbElementDao elementRep;
	@Autowired
	private EntityFactory entityFactory;
	
	private ElementEntity testElement;
	
	@Before
	public void addTestElement() {
		testElement = new ElementEntity();
		Map<String, Object> details = new HashMap<>();
		testElement = entityFactory.createNewElement("String name", "String type",
				new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail", "String creatorSmartspace",  
				true, details);
		elementRep.create(testElement);
	}

	@After
	public void cleanUp() {
		elementRep.deleteAll();
	}
	
	@Test
	public void addElement() {
		List<ElementEntity> elements = elementRep.readAll();
		assertThat(elements.get(0).getKey()).isEqualTo(testElement.getKey());
	}
	
	@Test
	public void readById() {
		String expectedKey = testElement.getKey();
		Optional<ElementEntity> elementRead = elementRep.readById(expectedKey);
		assert(elementRead.isPresent());
	}
	
	@Test
	public void readByWrongId() {
		Optional<ElementEntity> elementRead = elementRep.readById(testElement.getKey()+1);
		assert(!elementRead.isPresent());
	}
	
	@Test
	public void readAll() {
		Map<String, Object> details = new HashMap<>();
		ElementEntity elementToCreate2 = entityFactory.createNewElement("String name2", "String type2",
				new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail2", "String creatorSmartspace2",  
				true, details);
		elementRep.create(elementToCreate2);

		List<ElementEntity> elements = elementRep.readAll();
		
		assertThat(elements.size()).isEqualTo(2);
		
		ElementEntity secondUser = elements.get(1);
		
		checkElementsEquals(secondUser, elementToCreate2);
	}
	
	@Test
	public void updateSome() {
		testElement.setLocation(new Location(60,40));
		testElement.setType("String type");
		
		elementRep.update(testElement);
		
		List<ElementEntity> elements = elementRep.readAll();
		
		assertThat(elements.size()).isEqualTo(1);
		
		// Checking that the avatar was not changed
		assertThat(elements.get(0).getElementSmartspace()).isEqualTo(testElement.getElementSmartspace());
		assertThat(elements.get(0).getName()).isEqualTo(testElement.getName());
		assertThat(elements.get(0).getType()).isEqualTo(testElement.getType());
	}
	
	@Test
	public void updatePointsOnly() {
		testElement.setLocation(new Location(80,40));
		testElement.setType(null);
		testElement.setName(null);
		testElement.setCreatorEmail(null);
		
		elementRep.update(testElement);
		
		List<ElementEntity> elemnts = elementRep.readAll();
		
		assertThat(elemnts.size()).isEqualTo(1);
		
		// Checking that the avatar was not changed
		assertThat(elemnts.get(0).getElementSmartspace()).isEqualTo(testElement.getElementSmartspace());
		assertThat(elemnts.get(0).getElementId()).isEqualTo(testElement.getElementId());
		assertThat(elemnts.get(0).getType()).isEqualTo("String type");
	}
	
	
	private void checkElementsEquals(ElementEntity user1, ElementEntity user2) {
		assertThat(user1.getKey()).isEqualTo(user2.getKey());
		assertThat(user1.getElementSmartspace()).isEqualTo(user2.getElementSmartspace());
		assertThat(user1.getElementId()).isEqualTo(user2.getElementId());
		assertThat(user1.getName()).isEqualTo(user2.getName());
		assertThat(user1.getType()).isEqualTo(user2.getType());
	}
}
