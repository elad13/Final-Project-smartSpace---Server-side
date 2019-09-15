package smartspace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.ActionDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ActionDaoIntegrationTests {

	private ActionDao dao;

	@Autowired
	public void setDao(ActionDao dao) {
		this.dao = dao;
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
	public void testCreateWithNullAction() throws Exception {
		// GIVEN nothing

		// WHEN I create null message
		this.dao.create(null);

		// THEN create method throws excetpion
	}

}