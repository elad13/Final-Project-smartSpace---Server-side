package smartspace.infra;

import java.util.Collection;
import java.util.List;
import smartspace.data.ActionEntity;

public interface ActionService {

	public ActionEntity newAction(ActionEntity entity);
	public Collection<ActionEntity> store(String adminSmartspace, String adminEmail, Collection<ActionEntity> actionEntitiesToImport);
	public List<ActionEntity> getActionUsingPagination(String key, int size, int page);
	public ActionEntity invoke(ActionEntity newAction);
	public void deleteByKey(String key);
}
