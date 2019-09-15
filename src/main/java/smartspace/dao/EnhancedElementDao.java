package smartspace.dao;

import java.util.Date;
import java.util.List;
import smartspace.data.ElementEntity;
import smartspace.data.Location;

public interface EnhancedElementDao<Key> extends ElementDao<Key>{
	public List<ElementEntity> readAll(int size, int page);
	public List<ElementEntity> readAll(String sortBy, int size, int page);
	public List<ElementEntity> readElementWithNameContaining (String name, int size, int page);
	public List<ElementEntity> readElementWithTypeContaining (String type, int size, int page);
	public List<ElementEntity> readElementAvaiable (
			Date fromDate, Date toDate,
			int size, int page);
	public ElementEntity insert(ElementEntity elementEntity);
	public 	List<ElementEntity> readAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThan(
			Location minLocation, Location maxLocation, int size, int page);
	public void deleteById(String key);
}
