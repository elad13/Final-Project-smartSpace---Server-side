package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import smartspace.AppProperties;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
	
@Repository
public class RdbElementDao implements EnhancedElementDao<String>{
	private ElementCrud elementCrud;
	private GenericIdGeneratorCrud genericIdGeneratorCrud; 
	private AppProperties appProperties;
	
	
	
	
	@Autowired	
	public  RdbElementDao(
			ElementCrud elementCrud,
			GenericIdGeneratorCrud genericIdGeneratorCrud, AppProperties appProperties) {
		super();
		this.elementCrud = elementCrud;		
		this.genericIdGeneratorCrud = genericIdGeneratorCrud;
		this.appProperties = appProperties;
	}
	

	@Override
	@Transactional
	public ElementEntity create(ElementEntity elementEntity) {
		GenericIdGenerator nextId = 
				this.genericIdGeneratorCrud.save(new GenericIdGenerator());
			elementEntity.setKey(Long.toString(nextId.getId()) +"#"+ appProperties.getName());
			this.genericIdGeneratorCrud.delete(nextId);
		if (!this.elementCrud.existsById(elementEntity.getKey())) {
			 ElementEntity rv = this.elementCrud.save(elementEntity);
			return rv;
		}else {
			throw new RuntimeException("Element already exists with key: " + elementEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<ElementEntity> readById(String Key) {
	
		return this.elementCrud.findById(Key);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> readAll() {
		List<ElementEntity> rv = new ArrayList<>();
		this.elementCrud
			.findAll()
			.forEach(element->rv.add(element));
		return rv;
	}

	@Override
	@Transactional
	public void update(ElementEntity update) {   ////////
		ElementEntity existing = 
				this.readById(update.getKey())
					.orElseThrow(() -> new RuntimeException("not element to update"));
		if (update.getElementSmartspace() != null) {
			existing.setElementSmartspace(update.getElementSmartspace());
		}
		if (update.getElementId() != null) {
			existing.setElementId(update.getElementId());
		}
		if (update.getLocation() != null) {
			existing.setLocation(update.getLocation());
		}
		if (update.getName() != null) {
			existing.setName(update.getName());
		}
		if (update.getType() != null) {
			existing.setType(update.getType());
		}
		if (update.getCreationTimestamp() != null) {
			existing.setCreationTimestamp(update.getCreationTimestamp());
		}
		if (update.getCreatorSmartspace() != null) {
			existing.setCreatorSmartspace(update.getCreatorSmartspace());
		}
		if (update.getCreatorEmail() != null) {
			existing.setCreatorEmail(update.getCreatorEmail());
		}
		if (update.getMoreAttributes() != null) {
			existing.setMoreAttributes(update.getMoreAttributes());
		}
		// SQL: UPDATE
		this.elementCrud.save(existing);		
		
	}

	@Override
	@Transactional
	public void deleteByKey(String elementKey) {
		this.elementCrud.deleteById(elementKey);
	}

	@Override
	@Transactional
	public void delete(ElementEntity elementEntity) {
		this.elementCrud.delete(elementEntity);
		
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.elementCrud.deleteAll();
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> readAll(int size, int page) {
		return this.elementCrud
			.findAll(PageRequest.of(page, size))
			.getContent();
	}


	
	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> readElementWithTypeContaining(
			String type, 
			int size, 
			int page) {
		
		return this.elementCrud
				.findAllByTypeLike(
						type,
						PageRequest.of(page, size));
	}

	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> readElementAvaiable(
			Date fromDate, 
			Date toDate, 
			int size, int page) {
		return this.elementCrud
				.findAllByCreationTimestampBetween(
						fromDate, toDate,
						PageRequest.of(page, size));
	}

	@Override
	public List<ElementEntity> readAll(String sortBy, int size, int page) {
		return this.elementCrud
			.findAll(PageRequest.of(
					page, size, 
					Direction.ASC, sortBy))
			.getContent();
	}
	
	
	
	@Override
	@Transactional
	public ElementEntity insert(ElementEntity elementEntity) {
		if (!this.elementCrud.existsById(elementEntity.getKey())) {
			 ElementEntity rv = this.elementCrud.save(elementEntity);
			return rv;
		}else {
			this.update(elementEntity);
			return elementEntity;
		}
	}


	@Override
	public List<ElementEntity> readAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThan( // mongoDB throws Error while sending 2 locations at one time
			Location minLocation, Location maxLocation, int size, int page){
		List<ElementEntity> maxList = this.elementCrud.readAllByLocationXLessThanAndLocationYLessThan
				(maxLocation.getX(), maxLocation.getY(), PageRequest.of(page, size));
		List<ElementEntity> minList = this.elementCrud.readAllByLocationXGreaterThanAndLocationYGreaterThan
				(minLocation.getX(), minLocation.getY(), PageRequest.of(page, size));
		maxList.stream().filter(c -> minList.contains(c)).collect(Collectors.toList());
		return maxList;			
	}
	

	
	@Override
	@Transactional(readOnly=true)
	public List<ElementEntity> readElementWithNameContaining(
			String name, 
			int size, 
			int page) {
		
		return this.elementCrud
				.findAllByNameLike(
						name,
						PageRequest.of(page, size));
	}


	@Override
	public void deleteById(String key) {
		this.elementCrud.deleteById(key);
		
	}
	
}