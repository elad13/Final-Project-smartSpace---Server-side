package smartspace.data.util;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

@Component
public class EntityFactoryImpl implements EntityFactory {

	@Override
	public UserEntity createNewUser(String userEmail, String userSmartspace, String userName, String avatar,
			UserRole role, long points) {
		return new UserEntity(userEmail, userSmartspace, userName, avatar, role, points);
	}

	@Override
	public ElementEntity createNewElement(String name, String type, Location location, Date creationTimestamp,
			String creatorEmail, String creatorSmartspace, boolean expired, Map<String, Object> moreAttributes) {
		return new ElementEntity(name, type, location, creationTimestamp, creatorEmail, creatorSmartspace, expired,
				moreAttributes);
	}

	@Override
	public ActionEntity createNewAction(String elementId, String elementSmartspace, String actionType,
			Date creationTimestamp, String playerEmail, String playerSmartspace, Map<String, Object> moreAttributes) {
		return new ActionEntity(elementId, elementSmartspace, actionType, creationTimestamp, playerEmail,
				playerSmartspace, moreAttributes);
	}

}