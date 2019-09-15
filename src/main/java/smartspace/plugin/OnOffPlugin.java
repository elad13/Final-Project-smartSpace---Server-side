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
public class OnOffPlugin implements Plugin {

	private EnhancedUserDao<String> users;
	private EnhancedElementDao<String> elements;
	

	@Autowired
	public OnOffPlugin(EnhancedUserDao<String> users, EnhancedElementDao<String> elements) {
		this.users = users;
		this.elements = elements;
	}


	@Override
	public ActionEntity process(ActionEntity action) {

		try {
			ElementEntity currentElement = this.elements.readById(action.getElementId()+ "#" +action.getElementSmartspace())
					.orElseThrow(() -> new NullPointerException("Element Doesn't exist"));
			UserEntity user = users.readById(action.getPlayerEmail() + "#" + action.getPlayerSmartspace())
					.orElseThrow(() -> new NullPointerException("User Doesn't exist"));
			if(currentElement.getMoreAttributes().get("state").equals(null))
				throw new NullPointerException("You Can't turn On / Off this element");

			if (currentElement.getMoreAttributes().get("state").equals("On")) {
				currentElement.getMoreAttributes().remove("state");
				currentElement.getMoreAttributes().put("state", "Off");
			}
			else {
				currentElement.getMoreAttributes().remove("state");
				currentElement.getMoreAttributes().put("state", "On");
			}
		elements.update(currentElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return action;
	}
}
