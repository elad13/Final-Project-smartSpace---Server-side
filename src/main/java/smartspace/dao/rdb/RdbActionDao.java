package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.AppProperties;
import smartspace.dao.EnhancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;

@Repository
public class RdbActionDao implements EnhancedActionDao {

	private ActionCrud actionCrud;
	private GenericIdGeneratorCrud genericIdGeneratorCrud;
	private AppProperties appProperties;
	

	@Autowired
	public RdbActionDao(ActionCrud actionCrud, GenericIdGeneratorCrud genericIdGeneratorCrud, AppProperties appProperties) {
		super();
		this.actionCrud = actionCrud;
		this.genericIdGeneratorCrud = genericIdGeneratorCrud;
		this.appProperties = appProperties;
	}

	@Override
	@Transactional
	public ActionEntity create(ActionEntity actionEntity) {

		GenericIdGenerator nextId = 
				this.genericIdGeneratorCrud.save(new GenericIdGenerator());
			actionEntity.setKey(Long.toString(nextId.getId()) + "#" + appProperties.getName());
			this.genericIdGeneratorCrud.delete(nextId);
		if (!this.actionCrud.existsById(actionEntity.getKey())) {
			ActionEntity rv = this.actionCrud.save(actionEntity);
			return rv;
		}else {
			this.update(actionEntity);
			return actionEntity;
		}
	}

	@Override
	@Transactional
	public void update(ActionEntity update) {   ////////
		ActionEntity existing = 
				this.readById(update.getKey())
					.orElseThrow(() -> new RuntimeException("not action to update"));
		if (update.getActionType() != null) {
			existing.setActionType(update.getActionType());
		}
		if (update.getCreationTimestamp() != null) {
			existing.setCreationTimestamp(update.getCreationTimestamp());
		}
		if (update.getElementId() != null) {
			existing.setElementId(update.getElementId());
		}
		if (update.getElementSmartspace() != null) {
			existing.setElementSmartspace(update.getElementSmartspace());
		}
		if (update.getMoreAttributes() != null) {
			existing.setMoreAttributes(update.getMoreAttributes());
		}
		if (update.getPlayerEmail() != null) {
			existing.setPlayerEmail(update.getPlayerEmail());
		}
		if (update.getPlayerSmartspace() != null) {
			existing.setPlayerSmartspace(update.getPlayerSmartspace());
		}
		// SQL: UPDATE
		this.actionCrud.save(existing);		
		
	}
	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll() {
		List<ActionEntity> rv = new ArrayList<>();
		// SQL: SELECT
		this.actionCrud.findAll().forEach(rv::add);

		return rv;
	}

	@Override
	@Transactional
	public void deleteAll() {
		// SQL: DELETE
		this.actionCrud.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll(int size, int page) {
		return this.actionCrud.findAll(PageRequest.of(page, size)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionWithElementIdContaining(String elementId, int size, int page) {
		return this.actionCrud.findAllByElementId(elementId, PageRequest.of(page, size));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionAvaiable(Date fromDate, Date toDate, int size, int page) {
		return this.actionCrud.findAllByCreationTimestampBetween(fromDate, toDate, PageRequest.of(page, size));
	}

	@Override
	public List<ActionEntity> readAll(String sortBy, int size, int page) {
		return this.actionCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ActionEntity> readById(String actionKey) {
		return this.actionCrud.findById(actionKey);
	}
	
	
	@Override
	@Transactional
	public ActionEntity insert(ActionEntity actionEntity) {
		// SQL: INSERT INTO ACTION (ID, NAME) VALUES (?,?);
		if (!this.actionCrud.existsById(actionEntity.getKey())) {
			ActionEntity rv = this.actionCrud.save(actionEntity);
			return rv;
		} else {
			throw new RuntimeException("Action already exists with key: " + actionEntity.getKey());
		}
	}

	@Override
	public void deleteById(String key) {
		this.actionCrud.deleteById(key);
		
	}
}
