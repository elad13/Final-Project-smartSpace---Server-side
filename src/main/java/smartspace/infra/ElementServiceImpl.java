package smartspace.infra;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartspace.AppProperties;
import smartspace.dao.EnhancedElementDao;
import smartspace.dao.EnhancedUserDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserRole;

@Service
public class ElementServiceImpl implements ElementService {
	private EnhancedElementDao<String> elementDao;
	private EnhancedUserDao<String> userDao;
	private AppProperties appProperties;


	@Autowired
	public ElementServiceImpl(EnhancedElementDao<String> elementDao, EnhancedUserDao<String> userDao, AppProperties appProperties) {
		this.elementDao = elementDao;
		this.userDao = userDao;
		this.appProperties = appProperties;
	}

	@Override
	public ElementEntity newElement(ElementEntity entity, String key) {
			if (this.userDao.readById(key).isPresent() && this.userDao.readById(entity.getCreatorEmail()+"#"+entity.getCreatorSmartspace()).isPresent()) {
				if (this.userDao.readById(key).get().getRole() != UserRole.MANAGER) {
					throw new RuntimeException("you are not allowed to create elements");
				}
			} else
				throw new NoSuchElementException("your user doesn't exist");
		if (valiadate(entity)) {
			return this.elementDao.create(entity);
		} else {
			throw new RuntimeException("invalid user");
		}
	}

	private boolean valiadate(ElementEntity entity) {
		return entity != null && !entity.getName().trim().isEmpty() && entity.getCreatorEmail() != null;
	}

	@Override
	public List<ElementEntity> getElementUsingPagination(String userKey, int size, int page) {
		if (this.userDao.readById(userKey).isPresent()) {
			if(!this.userDao.readById(userKey).get().getUserSmartspace().equals(appProperties.getName())) {
				if (this.userDao.readById(userKey).get().getRole() != UserRole.ADMIN) {
					throw new RuntimeException("you are not allowed to get users");
				}
			}
		}
		else {
				throw new NoSuchElementException("your user doesn't exist");
		}
		return this.elementDao.readAll("key", size, page);
	}
	
	@Override
	public ElementEntity retrieveElement(String userKey, String elementKey) {
		if (this.userDao.readById(userKey).isPresent()) {
			if(elementKey != null)
				return this.elementDao.readById(elementKey).get();
			else 
				throw new NoSuchElementException("Null Element Key");
		} else
			throw new NoSuchElementException("your user doesn't exist");
	}
	
	
	@Override
	@Transactional
	public Collection<ElementEntity> store(String adminSmartspace, String adminEmail,
			Collection<ElementEntity> elementEntitiesToImport){
		if(this.userDao.getUserRole(adminSmartspace, adminEmail) != UserRole.ADMIN) {
			throw new RuntimeException("You are not allowed to create users");
		} else {
			if(elementEntitiesToImport.stream().anyMatch(entity-> entity.getCreatorEmail().equals(appProperties.getName()))){
				throw new RuntimeException("Not allowed to import data from the local smartspace");
			}
			
			return elementEntitiesToImport.stream()
					.map(entity-> elementDao.insert(entity))
					.collect(Collectors.toList());
		}
	}
	
	
	@Override
	public void update(String userEmail, String userSmartspace, String elementKey, ElementEntity element) {
		if(this.userDao.getUserRole(userSmartspace, userEmail) != UserRole.MANAGER)
			throw new RuntimeException("You are not allowed to update users");
		if(element.getKey()!=null) {
			element.setKey(elementKey);
			this.elementDao
				.update(element);
		}
		else {
			throw new RuntimeException("Can't re-write key value, must be Null");
		}
	}
		
	
	@Override
	public void validateAuthorization(UserRole role) {
		if (!role.equals(UserRole.MANAGER)) {
			throw new RuntimeException("Only managers are authorized to create new elements");
		}
	}
	
	@Override
	@Transactional
	public List<ElementEntity> getAllNearby(int size, int page, double x, double y, double distance) {
		double minX = x - distance;
		double maxX = x + distance;
		double minY = y - distance;
		double maxY = y + distance;
			return elementDao.readAllByLocationXGreaterThanAndLocationXLessThanAndLocationYGreaterThanAndLocationYLessThan(
					new Location(minX, minY), new Location(maxX, maxY), size, page);
		}
	
	@Override
	@Transactional
	public List<ElementEntity> getAllByName(int size,int page,String elementName) {
			return elementDao.readElementWithNameContaining(elementName, size, page);
	}
	
	@Override
	@Transactional
	public List<ElementEntity> getAllByType(int size,int page,String type) {
			return elementDao.readElementWithTypeContaining(type, size, page);
	}

	@Override
	public void deleteByKey(String key) {
		this.elementDao.deleteById(key);
		
	}
	
	
	
	
}