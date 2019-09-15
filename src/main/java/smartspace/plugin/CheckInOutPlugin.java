package smartspace.plugin;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import smartspace.dao.ElementDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.dao.UserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;

@Component
public class CheckInOutPlugin implements Plugin {

	private EnhancedUserDao<String> users;
	private EnhancedElementDao<String> elements;
	private final int POINTS_FOR_OPEN_DOOR = 5;

	@Autowired
	public CheckInOutPlugin(EnhancedUserDao<String> users, EnhancedElementDao<String> elements) {
		this.users = users;
		this.elements = elements;
	}


	@Override
	public ActionEntity process(ActionEntity action) {

		try {
			ElementEntity doorElement = this.elements.readById(action.getElementId()+"#"+action.getElementSmartspace())
					.orElseThrow(() -> new NullPointerException("Element Doesn't exist"));
			UserEntity user = users.readById(action.getPlayerEmail() + "#" + action.getPlayerSmartspace())
					.orElseThrow(() -> new NullPointerException("User Doesn't exist"));
			if (!doorElement.getType().equals("Door"))
				throw new NullPointerException("This Element isn't a Door!");
			user.setPoints(user.getPoints() + POINTS_FOR_OPEN_DOOR);
			users.update(user);

			if (doorElement.getMoreAttributes().get("status").equals("Open")) {
				doorElement.getMoreAttributes().remove("status");
				doorElement.getMoreAttributes().put("status", "Close");
			}
			else {
				doorElement.getMoreAttributes().remove("status");
				doorElement.getMoreAttributes().put("status", "Open");
			}
		elements.update(doorElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return action;
	}
}
