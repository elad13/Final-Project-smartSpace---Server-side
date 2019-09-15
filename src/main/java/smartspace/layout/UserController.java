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
import smartspace.data.UserEntity;
import smartspace.infra.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	
	@RequestMapping(path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] importUsers(@RequestBody UserBoundary[] usersToImport,
			@PathVariable("adminSmartspace") String adminSmartspace, 
			@PathVariable("adminEmail") String adminEmail) {
		Collection<UserEntity> userEntitiesToImport = ((Arrays.asList(usersToImport))).stream()
				.map(user -> new UserBoundary()
				.convertToEntity(user))
				.collect(Collectors.toList());

		return this.userService.store(adminSmartspace, adminEmail, userEntitiesToImport).stream().map(UserBoundary::new)
				.collect(Collectors.toList()).toArray(new UserBoundary[0]);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] getUserUsingPagination(@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.userService.getUserUsingPagination(adminSmartspace, adminEmail, size, page).stream()
				.map(UserBoundary::new).collect(Collectors.toList()).toArray(new UserBoundary[0]);
	}
	
	@RequestMapping(
			path="/smartspace/users",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary newUser (
			@RequestBody NewUserForm user){
		return new UserBoundary(
				this.userService
					.newUser(user.convertToEntity(user)));
	}
	
	@RequestMapping(
			method = RequestMethod.GET,
			path="/smartspace/users/login/{userSmartspace}/{userEmail}",
			produces=MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary loginUser (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail){
		return new UserBoundary(
			this.userService.login(userEmail, userSmartspace));
	}
	
	@RequestMapping(
			path="/smartspace/users/login/{userSmartspace}/{userEmail}",
			method=RequestMethod.PUT,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public void update (
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestBody UserBoundary updateUser) {
		this.userService.updateWithoutPoints(userEmail +"#"+ userSmartspace, updateUser.convertToEntity(updateUser));
	}
	
	@RequestMapping(
			path="/smartspace/users/delete/{key}",
			method=RequestMethod.DELETE)
	public void deleteByKey(
			@PathVariable("key") String key) {
		this.userService
			.deleteByKey(key);
	}
	
}
