package smartspace.layout;

import java.util.Date;
import java.util.Map;
import smartspace.data.ActionEntity;

public class ActionBoundary {

	private BoundaryIdKey actionKey;
	private BoundaryIdKey element;
	private BoundaryEmailKey player;
	private String type;
	private Date created;
	private Map<String, Object> properties;

	public ActionBoundary() {
	}

	public ActionBoundary(ActionEntity entity) {

		if (entity.getKey() != null)
			this.actionKey = new BoundaryIdKey(entity.getActionId(), entity.getActionSmartspace());
		else
			throw new NullPointerException("Null Action ID");
		this.type = entity.getActionType();
		this.created = entity.getCreationTimestamp();
		if (entity.getElementId() != null && entity.getElementSmartspace() != null)
			this.element = new BoundaryIdKey(entity.getElementId(), entity.getElementSmartspace());
		else
			throw new NullPointerException("Null Element ID");
		if (entity.getPlayerSmartspace() != null && entity.getPlayerEmail() != null)
			this.player = new BoundaryEmailKey(entity.getPlayerEmail(), entity.getPlayerSmartspace());
		else
			throw new NullPointerException("Null Player ID");
		this.properties = entity.getMoreAttributes();

	}

	public BoundaryIdKey getActionKey() {
		return actionKey;

	}

	public void setActionKey(BoundaryIdKey key) {
		this.actionKey = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String actionType) {
		this.type = actionType;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date creationTimestamp) {
		this.created = creationTimestamp;
	}

	public BoundaryIdKey getElement() {
		return element;
	}

	public void setElement(BoundaryIdKey element) {
		this.element = element;
	}

	public BoundaryEmailKey getPlayer() {
		return player;
	}

	public void setPlayer(BoundaryEmailKey player) {
		this.player = player;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> moreAttributes) {
		this.properties = moreAttributes;
	}

	public ActionEntity convertToEntity(ActionBoundary action) {
		ActionEntity entity = new ActionEntity();
		if (action.getActionKey() != null) {
			entity.setKey(action.getActionKey().toString());
		}
		if (action.getElement() != null) {
			entity.setElementId(action.getElement().getId());
			entity.setElementSmartspace(action.getElement().getSmartspace());
		} else
			throw new NullPointerException("Null Element");
		if (action.getPlayer() != null) {
			entity.setPlayerEmail(action.getPlayer().getEmail());
			entity.setPlayerSmartspace(action.getPlayer().getSmartspace());
		} else
			throw new NullPointerException("Null Element");
		entity.setActionType(action.getType());
		entity.setCreationTimestamp(action.getCreated());
		entity.setMoreAttributes(action.getProperties());

		return entity;
	}

}
