package smartspace.layout;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smartspace.data.ElementEntity;
import smartspace.infra.ElementService;
import smartspace.infra.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ElementController {
	private ElementService elementService;
	private UserService userService;

	@Autowired
	public ElementController(ElementService elementService) {
		this.elementService = elementService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	
	
	@RequestMapping(
			path="/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] newElement (
			@RequestBody ElementBoundary[] elementsToImport, 
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail){
		Collection<ElementEntity> elementEntitiesToImport = ((Arrays.asList(elementsToImport))).stream()
				.map(element -> new ElementBoundary().convertToEntity(element)).collect(Collectors.toList());

		return this.elementService
				.store(adminSmartspace, adminEmail, elementEntitiesToImport)
				.stream()
				.map(ElementBoundary::new)
				.collect(Collectors.toList()).
				toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/elements/{managerSmartspace}/{managerEmail}",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary newElement (
			@RequestBody ElementBoundary createElement, 
			@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail){
			if(createElement.getKey().getId() == null && createElement.getKey().getSmartspace() == null) {
					return new ElementBoundary(
							this.elementService
							.newElement(createElement.convertToEntity(createElement),  managerEmail +"#"+ managerSmartspace));}
			else {
				throw new RuntimeException("Element must be with a null key");
			}
	}
	

	@RequestMapping(
			path="/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getElementUsingPagination (
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return 
			this.elementService
			.getElementUsingPagination(adminEmail + "#" + adminSmartspace, size, page)
			.stream()
			.map(ElementBoundary::new)
			.collect(Collectors.toList())
			.toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
			method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void update (
			@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId,
			@RequestBody ElementBoundary updateElement) {
		if(updateElement.getKey().getSmartspace() == null && updateElement.getKey().getId() ==null)
			this.elementService.update(managerEmail, managerSmartspace,elementId +"#"+ elementSmartspace,
					updateElement.convertToEntity(updateElement));
		else
			throw new NullPointerException("element key must be null");
	}
	
	@RequestMapping(
			path="/smartspace/elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary retrieveElement (
			@PathVariable("userSmartspace") String adminSmartspace,
			@PathVariable("userEmail") String adminEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId) {
		return new ElementBoundary(this.elementService
			.retrieveElement(adminEmail +"#" + adminSmartspace, elementId + "#" + elementSmartspace));
	}
	
	@RequestMapping(
			path="/smartspace/elements/{userSmartspace}/{userEmail}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE,
			params= {"size","page","!search","!x","!y"})
	public ElementBoundary[] getAllElementUsingPagination (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		return 
			this.elementService
			.getElementUsingPagination(userEmail + "#" + userSmartspace, size, page)
			.stream()
			.map(ElementBoundary::new)
			.collect(Collectors.toList())
			.toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/elements/{userSmartspace}/{userEmail}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE,
			params = {"search=location"})
	public ElementBoundary[] getAllElementsNearLocation (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name="search", required=false) String location,
			@RequestParam(name="x", required=false, defaultValue="5") int x,
			@RequestParam(name="y", required=false, defaultValue="5") int y,
			@RequestParam(name="distance", required=false, defaultValue="10") int distance,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
			userService.login(userEmail, userSmartspace);
		return this.elementService.getAllNearby(size, page, x, y, distance).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/elements/{userSmartspace}/{userEmail}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE,
			params = {"search=name"})
	public ElementBoundary[] getAllElementsWithSpecifiedName (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name="search", required=false) String name,
			@RequestParam(name="value", required=true) String elementName,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
			userService.login(userEmail, userSmartspace);
		return this.elementService.getAllByName(size, page, elementName).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/elements/{userSmartspace}/{userEmail}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE,
			params = {"search=type"})
	public ElementBoundary[] getAllElementsWithSpecifiedType (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name="search", required=false) String name,
			@RequestParam(name="value", required=true) String type,
			@RequestParam(name="size", required = false, defaultValue = "10") int size,
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
			userService.login(userEmail, userSmartspace);
		return this.elementService.getAllByType(size, page, type).stream().map(ElementBoundary::new)
				.collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
	
	
	@RequestMapping(
			path="/smartspace/elements/delete/{key}",
			method=RequestMethod.DELETE)
	public void deleteByKey(
			@PathVariable("key") String key) {
		this.elementService
			.deleteByKey(key);
	}
	

	
	
	
	
}
