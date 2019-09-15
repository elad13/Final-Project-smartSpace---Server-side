package smartspace.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;

public interface EnhancedActionDao extends ActionDao{
	public List<ActionEntity> readAll(int size, int page);
	public List<ActionEntity> readAll(String sortBy, int size, int page);
	public List<ActionEntity> readActionWithElementIdContaining (String text, int size, int page);
	public List<ActionEntity> readActionAvaiable (
			Date fromDate, Date toDate,
			int size, int page);
	Optional<ActionEntity> readById(String actionKey);
	public ActionEntity insert(ActionEntity actionEntity);
	public void update(ActionEntity update);
	public void deleteById(String key);

}
