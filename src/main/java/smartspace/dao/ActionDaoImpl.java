package smartspace.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import smartspace.data.ActionEntity;

//@Repository
public class ActionDaoImpl implements ActionDao {
	private List<ActionEntity> actions;
	private AtomicLong nextId;

	public ActionDaoImpl() {
		this.actions = Collections.synchronizedList(new ArrayList<>());
		this.nextId = new AtomicLong(1);
	}

	@Override
	public ActionEntity create(ActionEntity actionEntity) {
		actionEntity.setKey(Long.toString(nextId.getAndIncrement()) +"#"+ actionEntity.getActionSmartspace());
		this.actions.add(actionEntity);
		return actionEntity;
	}

	@Override
	public List<ActionEntity> readAll() {
		return this.actions;
	}

	@Override
	public void deleteAll() {
		this.actions.clear();
	}

}
