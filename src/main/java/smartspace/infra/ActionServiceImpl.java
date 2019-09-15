package smartspace.infra;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.AppProperties;
import smartspace.dao.EnhancedActionDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.UserRole;
import smartspace.plugin.Plugin;

@Service
public class ActionServiceImpl implements ActionService {
	private EnhancedActionDao actionDao;
	private EnhancedElementDao<String> elementDao;
	private EnhancedUserDao<String> userDao;
	private AppProperties appProperties;
	private ApplicationContext ctx;

	@Autowired
	public ActionServiceImpl(EnhancedActionDao actionDao, EnhancedElementDao<String> elementDao,
			EnhancedUserDao<String> userDao, AppProperties appProperties, ApplicationContext ctx) {
		this.actionDao = actionDao;
		this.elementDao = elementDao;
		this.userDao = userDao;
		this.appProperties = appProperties;
		this.ctx = ctx;
	}

	@Override
	public ActionEntity newAction(ActionEntity entity) {

		if (!entity.getPlayerSmartspace().equals(appProperties.getName()))
			throw new NoSuchElementException("Wrong smartspace name, has to be with local smartspace name");

		if (!this.elementDao.readById((entity.getElementId()) + "#" + entity.getElementSmartspace()).isPresent()) {
			throw new RuntimeException("You have to set Element before");
		}

		if (valiadate(entity)) {
			return this.actionDao.create(entity);
		} else {
			throw new RuntimeException("invalid action");
		}

	}

	@Override
	@Transactional
	public Collection<ActionEntity> store(String adminSmartspace, String adminEmail,
			Collection<ActionEntity> actionEntitiesToImport) {
		if (this.userDao.getUserRole(adminSmartspace, adminEmail) != UserRole.ADMIN) {
			throw new RuntimeException("You are not allowed to create users");
		} else {
			if (actionEntitiesToImport.stream()
					.anyMatch(entity -> entity.getPlayerSmartspace().equals(appProperties.getName()))) {
				throw new RuntimeException("Not allowed to import data from the local smartspace");
			}

			return actionEntitiesToImport.stream().map(entity -> actionDao.insert(entity)).collect(Collectors.toList());
		}
	}

	@Override
	@Transactional
	public ActionEntity invoke(ActionEntity newAction) {
		try {
			String actionType = newAction.getActionType();
			if (actionType != null && !actionType.trim().isEmpty()) {
				if (valiadate(newAction)) {
					newAction.setCreationTimestamp(new Date());
					String className = "smartspace.plugin." + actionType.toUpperCase().charAt(0)
							+ actionType.substring(1) + "Plugin";
					Class<?> theClass = Class.forName(className);
					Plugin plugin = (Plugin) ctx.getBean(theClass);
					newAction = plugin.process(newAction);
					return this.actionDao.create(newAction);
				} else {
					throw new RuntimeException("Action is invalid - Wrong element of user");
				}
			} else {
				throw new RuntimeException("Illegal action type");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean valiadate(ActionEntity entity) {
		if (entity.getPlayerEmail() != null && entity.getPlayerSmartspace() != null && entity.getElementId() != null
				&& entity.getElementSmartspace() != null && entity.getActionType() != null) {
			if (this.userDao.readById(entity.getPlayerEmail() + "#" + entity.getPlayerSmartspace()).isPresent()
					&& this.elementDao.readById(entity.getElementId() + "#" + entity.getElementSmartspace())
							.isPresent()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ActionEntity> getActionUsingPagination(String key, int size, int page) {
		if (this.userDao.readById(key).isPresent()) {
			if (this.userDao.readById(key).get().getRole() != UserRole.ADMIN) {
				throw new RuntimeException("You are not allowed to get users");
			}
		} else
			throw new NoSuchElementException("Your user doesn't exist");
		return this.actionDao.readAll("key", size, page);
	}

	@Override
	public void deleteByKey(String key) {
		this.actionDao.deleteById(key);
		
	}

}
