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

import smartspace.data.ActionEntity;
import smartspace.infra.ActionService;
import smartspace.layout.ActionBoundary;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ActionController {
	private ActionService actionService;

	@Autowired
	public ActionController(ActionService actionService) {
		this.actionService = actionService;
	}

	@RequestMapping(path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] importActions(
			@RequestBody ActionBoundary[] actionsToImport,
			@PathVariable("adminSmartspace") String adminSmartspace, 
			@PathVariable("adminEmail") String adminEmail) {
		Collection<ActionEntity> actionEntitiesToImport = ((Arrays.asList(actionsToImport))).stream()
				.map(action -> new ActionBoundary().convertToEntity(action)).collect(Collectors.toList());

		return this.actionService
				.store(adminSmartspace, adminEmail, actionEntitiesToImport)
				.stream()
				.map(ActionBoundary::new)
				.collect(Collectors.toList()).
				toArray(new ActionBoundary[0]);
	}

	@RequestMapping(path = "/smartspace/admin/actions/{adminSmartspace}/{adminEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary[] getUsingPagination(@PathVariable(name = "adminSmartspace") String adminSmartspace,
			@PathVariable(name = "adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.actionService.getActionUsingPagination(adminEmail + "#" + adminSmartspace, size, page).stream()
				.map(ActionBoundary::new).collect(Collectors.toList()).toArray(new ActionBoundary[0]);
	}
	
	
	@RequestMapping(
			path="/smartspace/actions",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ActionBoundary newAction (
			@RequestBody ActionBoundary action){
		if(action.getActionKey().getId() == null && action.getActionKey().getSmartspace() == null)
			return new ActionBoundary(
					this.actionService
						.invoke(action.convertToEntity(action)));
		else {
			throw new NullPointerException("action key must be Null");
		}
	}

	@RequestMapping(
			path="/smartspace/actions/delete/{key}",
			method=RequestMethod.DELETE)
	public void deleteByKey(
			@PathVariable("key") String key) {
		this.actionService
			.deleteByKey(key);
	}
}
