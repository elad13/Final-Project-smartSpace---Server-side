package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
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

import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactoryImpl;

//Failed Test!

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementDaoIntegrationTests {
	private ElementDao<String> dao;
	private EntityFactoryImpl factory;

	@Autowired
	public void setDao(ElementDao<String> dao) {
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
	public void testCreateWithNullElement() throws Exception {
		// GIVEN nothing

		// WHEN I create null element
		this.dao.create(null);

		// THEN create method throws exeption
	}

	@Test
	public void testCreateGetByIdWithValidElement() throws Throwable {
		// GIVEN dao is initialized and clean

		Map<String, Object> details = new HashMap<>();
		details.put("test", new Boolean(true));
		//Map<String, Object> moreAttributes;
		ElementEntity element = this.factory.createNewElement("String name", "String type",
				new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail", "String creatorSmartspace",  
				true, details);
		ElementEntity elementInDB = this.dao.create(element);

		ElementEntity elementFromDB = (ElementEntity) this.dao.readById(elementInDB.getKey())
				.orElseThrow(() -> new RuntimeException("could not find element by key"));

		// THEN the same element is returned
		assertThat(elementFromDB).isNotNull().extracting("name", "type").containsExactly("String name",
				"String type");

	}

	@Test
	public void testCreateUpdateReadByIdDeleteAllReadAll() throws Throwable {
		// GIVEN nothing

		// WHEN I create a new element
		// AND update element
		// AND get element by key
		// AND Delete all elements
		// AND read all
		Map<String, Object> details = new HashMap<>();
		details.put("title", "hello");
		details.put("signature", 2123154546416841684L);
		details.put("langauge", "EN");
		details.put("interesting", false);
		ElementEntity element1 = this.factory.createNewElement("String name1", "String type1",
				new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail1", "String creatorSmartspace1",  
				true, details);
		//element1.setText("demo element");
		element1 = this.dao.create(element1);

		
		  ElementEntity update = this.factory.createNewElement("String name2", "String type2",
					new Location(3, 2), new Date(System.currentTimeMillis()), "String creatorEmail2", "String creatorSmartspace2",  
					true, details);
		  update.setKey(element1.getKey());
		  //Map<String, Object> updatedDetails = new HashMap<>(details);
		  this.dao.update(update);
		 
		this.dao.readById(element1.getKey())
		.orElseThrow(() -> new RuntimeException("not element after update"));

		this.dao.deleteAll();

		List<ElementEntity> list = this.dao.readAll();

		// THEN the created element received an id > 0
		// AND the dao contains nothing
		assertThat(element1.getKey() != null);
		//ElementEntity temp = this.dao.readById(element1.getKey()).orElseThrow(() -> new RuntimeException("not element after first assert"));
		assertThat(list).isEmpty();

	}
}
