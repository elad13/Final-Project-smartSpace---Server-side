package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.rdb.RdbActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class ActionRdbIntegrationTest {
	
	@Autowired
	private RdbActionDao rdbActionDao;
	@Autowired
	private EntityFactory entityFactory;

	private ActionEntity testAction;
	
	@Before
	public void addTestAction() {
		testAction = new ActionEntity();
		
		testAction = entityFactory.createNewAction("elementId",
				"2019B.giron.aptik",
				"actionType",
				new Date(),
				"email@gmail.com",
				"userName",null);
		rdbActionDao.create(testAction);
	}
	
	@After
	public void cleanUp() {
		rdbActionDao.deleteAll();
	}

	@Test
	public void addAction() {
		List<ActionEntity> actions = rdbActionDao.readAll();
		assertThat(actions.get(0).getKey()).isEqualTo
		(testAction.getKey());
	}
	
	@Test
	public void readAll() {
		ActionEntity actionToCreate2 = entityFactory.createNewAction("elementId2",
				"2019B.giron.aptik",
				"actionType2",
				new Date(),
				"email@gmail.com2",
				"userName2",null);

		rdbActionDao.create(actionToCreate2);

		List<ActionEntity> actions = rdbActionDao.readAll();
		
		assertThat(actions.size()).isEqualTo(2);
		
		ActionEntity firstAction = actions.get(0);
		ActionEntity secondActionr = actions.get(1);
		
		checkActionsEquals(firstAction, testAction);
		checkActionsEquals(secondActionr, actionToCreate2);
	}
	
	private void checkActionsEquals(ActionEntity action1, ActionEntity action2) {
		assertThat(action1.getActionSmartspace()).isEqualTo(action2.getActionSmartspace());
		assertThat(action1.getActionId()).isEqualTo(action2.getActionId());
		assertThat(action1.getElementSmartspace()).isEqualTo(action2.getElementSmartspace());
		assertThat(action1.getElementId()).isEqualTo(action2.getElementId());
		assertThat(action1.getPlayerSmartspace()).isEqualTo(action2.getPlayerSmartspace());
		assertThat(action1.getPlayerEmail()).isEqualTo(action2.getPlayerEmail());
		assertThat(action1.getActionType()).isEqualTo(action2.getActionType());
	}
	
}
