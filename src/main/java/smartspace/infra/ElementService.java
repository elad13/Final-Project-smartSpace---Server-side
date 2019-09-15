package smartspace.infra;

import java.util.Collection;
import java.util.List;
import smartspace.data.ElementEntity;
import smartspace.data.UserRole;

public interface ElementService {
	public ElementEntity newElement(ElementEntity entity, String elementKey);

	public Collection<ElementEntity> store(String adminSmartspace, String adminEmail,
			Collection<ElementEntity> actionEntitiesToImport);

	public List<ElementEntity> getElementUsingPagination(String userKey, int size, int page);

	public void update(String userEmail, String userSmartspace, String elementKey, ElementEntity element);

	public ElementEntity retrieveElement(String userKey, String elementKey);

	public void validateAuthorization(UserRole role);

	public List<ElementEntity> getAllNearby(int size, int page, double x, double y, double distance);

	public List<ElementEntity> getAllByName(int size,int page,String elementName);
	
	public List<ElementEntity> getAllByType(int size,int page,String type);

	public void deleteByKey(String key);

}
