package smartspace.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Convert;
import javax.persistence.Lob;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import smartspace.dao.rdb.MapToJsonConverter;

//@Entity
//@Table(name = "ACTIONS")
@Document(collection="ACTIONS")
public class ActionEntity implements SmartSpaceEntity<String> {

	private String actionSmartspace;
	private String actionId;
	private String elementSmartspace;
	private String elementId;
	private String playerSmartspace;
	private String playerEmail;
	private String actionType;
	private Date creationTimestamp;
	private Map<String, Object> moreAttributes;
	private String key;

	public ActionEntity() {
		moreAttributes = new HashMap<>();
	}

	public ActionEntity(String elementId, String elementSmartspace, String actionType, Date creationTimestamp,
			String playerEmail, String playerSmartspace, Map<String, Object> moreAttributes) {
		super();
		this.elementId = elementId;
		this.elementSmartspace = elementSmartspace;
		this.actionType = actionType;
		this.creationTimestamp = creationTimestamp;
		this.playerEmail = playerEmail;
		this.playerSmartspace = playerSmartspace;
		this.moreAttributes = moreAttributes;
	}

	public String getActionSmartspace() {
		return actionSmartspace;
	}

	public void setActionSmartspace(String actionSmartspace) {
		this.actionSmartspace = actionSmartspace;
	}
	@JsonIgnore
	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getElementSmartspace() {
		return elementSmartspace;
	}

	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementid) {
		this.elementId = elementid;
	}

	public String getPlayerSmartspace() {
		return playerSmartspace;
	}

	public void setPlayerSmartspace(String playerSmartspace) {
		this.playerSmartspace = playerSmartspace;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

//	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	@Lob
	@Convert(converter = MapToJsonConverter.class)
	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	public void addToAttributesMap(String attributeName, Object attributeObj) {
		this.moreAttributes.put(attributeName, attributeObj);
	}

	//@Column(name = "ID")
	//@Id
	@Override
	@org.springframework.data.annotation.Id
	public String getKey() {
		return this.key;
		//return this.actionId + "#" + this.actionSmartspace;
	}

	@Override
	public void setKey(String key) {
		String[] tmpArr = key.split("#");
		this.actionId = tmpArr[0];
		this.actionSmartspace = tmpArr[1];
		this.key = key;
	}

	@Override
	public String toString() {
		return "Action [ Id" + this.actionId + ", SmartSpace - " + this.actionSmartspace + ", action type - "
				+ this.actionType + ", element Smartspace - " + this.elementSmartspace + ", element ID - "
				+ this.elementId + ", playerSmartspace - " + this.playerSmartspace + ", player email - "
				+ this.playerEmail + ", creationTimestamp - " + this.creationTimestamp + ", more attributes - "
				+ this.moreAttributes.toString() + "]";
	}
}
