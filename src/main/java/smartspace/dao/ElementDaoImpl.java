package smartspace.dao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;

//@Repository
public class ElementDaoImpl implements ElementDao<String>{
	private List<ElementEntity> elements;
	private AtomicLong nextId;

	public ElementDaoImpl() {
		this.elements = Collections.synchronizedList(new ArrayList<>());
		this.nextId = new AtomicLong(1);
	}
	
	protected List<ElementEntity> getElements (){
		return this.elements;
	}
	
	@Override
	public ElementEntity create(ElementEntity elementEntity) {
		elementEntity.setKey(Long.toString(nextId.getAndIncrement()) +"#"+ elementEntity.getElementSmartspace());
		this.elements.add(elementEntity);
		return elementEntity;
	}
	
	@Override
	public Optional<ElementEntity> readById(String elementKey) {
		ElementEntity target = null;
		for (ElementEntity current : this.elements) {
			if (current.getKey().equals(elementKey)) {
				target = current;
			}
		}
		if (target != null) {
			return Optional.of(target);
		}else {
			return Optional.empty();
		}
	}
	@Override
	public List<ElementEntity> readAll() {
		return this.elements;

	}
	@Override
	public void update(ElementEntity update) {
		synchronized (this.elements) {
			ElementEntity existing = this.readById(update.getKey())
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
		}		
	}
	
	@Override
	public void deleteByKey(String elementKey) { 
		this.elements.remove(elementKey);
	}
	
	@Override
	public void delete(ElementEntity elementEntity) {
		this.elements.remove(elementEntity);	
	}
	@Override
	public void deleteAll() {
		this.elements.clear();			
	}

}
