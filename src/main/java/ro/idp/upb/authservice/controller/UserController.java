/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.controller;

import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.idp.upb.authservice.data.dto.response.UserDto;
import ro.idp.upb.authservice.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	@Secured({"ADMIN", "MANAGER"})
	public @ResponseBody UserDto me() throws LoginException {
		return userService.getUserDto();
	}
}
