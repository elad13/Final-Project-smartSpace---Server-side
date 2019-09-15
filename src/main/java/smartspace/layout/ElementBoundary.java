package smartspace.layout;

import java.util.Date;
import java.util.Map;
import smartspace.data.ElementEntity;
import smartspace.data.Location;

public class ElementBoundary {

	private BoundaryIdKey key;
	private String elementType;
	private String name;
	private boolean expired;
	private Date created;
	private BoundaryEmailKey creator;
	private LocationBoundary latlng;
	private Map<String, Object> elementProperties;

	public ElementBoundary() {
	}

	public ElementBoundary(ElementEntity entity) {

		if (entity.getKey() != null) {
			String ekey = entity.getElementId() + "#" + entity.getElementSmartspace();
			String[] tmpArr = ekey.split("#");
			this.key = new BoundaryIdKey(tmpArr[0], tmpArr[1]);
		} else
			throw new NullPointerException("Null Key");
		this.elementType = entity.getType();
		this.name = entity.getName();
		this.expired = entity.isExpired();
		this.created = entity.getCreationTimestamp();
		if (entity.getCreatorEmail() != null && entity.getCreatorSmartspace() != null)
			this.creator = new BoundaryEmailKey(entity.getCreatorEmail(), entity.getCreatorSmartspace()); // UKey
		else
			throw new NullPointerException("Null Creator");
		if (entity.getLocation() != null)
			this.latlng = new LocationBoundary(entity.getLocation().getX(), entity.getLocation().getY());
		else
			throw new NullPointerException("Null Latlng");
		this.elementProperties = entity.getMoreAttributes();

	}

	public BoundaryIdKey getKey() {
		return key;
	}

	public void setKey(BoundaryIdKey key) {
		this.key = key;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public BoundaryEmailKey getCreator() {
		return creator;
	}

	public void setCreator(BoundaryEmailKey creator) {
		this.creator = creator;
	}

	public LocationBoundary getLatlng() {
		return latlng;
	}

	public void setLatlng(LocationBoundary latlng) {
		this.latlng = latlng;
	}

	public Map<String, Object> getElementProperties() {
		return elementProperties;
	}

	public void setElementProperties(Map<String, Object> elementProperties) {
		this.elementProperties = elementProperties;
	}

	public ElementEntity convertToEntity(ElementBoundary element) {
		ElementEntity entity = new ElementEntity();
		if (element.getKey() != null) {
			entity.setKey(element.getKey().toString());
		}
		entity.setType(element.getElementType());
		entity.setName(element.getName());
		entity.setExpired(element.getExpired());
		entity.setCreationTimestamp(element.getCreated());
		if (element.getCreator() != null) {
			entity.setCreatorEmail(element.getCreator().getEmail());
			entity.setCreatorSmartspace(element.getCreator().getSmartspace());
		} else
			throw new NullPointerException("Null Creator");
		if (element.getLatlng() != null)
			entity.setLocation(new Location(element.getLatlng().getLat(), element.getLatlng().getLng()));
		else
			throw new NullPointerException("Null Location");

		entity.setMoreAttributes(element.getElementProperties());

		return entity;
	}

}
