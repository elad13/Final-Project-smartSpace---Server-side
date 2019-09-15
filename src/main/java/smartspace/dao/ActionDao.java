package smartspace.dao;

import smartspace.data.ActionEntity;

public interface ActionDao {

	public ActionEntity create(ActionEntity actionEntity);
	public java.util.List<ActionEntity> readAll();	
	public void deleteAll();
}
