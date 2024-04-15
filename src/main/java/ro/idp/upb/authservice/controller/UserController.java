package ro.idp.upb.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.idp.upb.authservice.config.SecurityUtils;
import ro.idp.upb.authservice.data.dto.response.UserDto;
import ro.idp.upb.authservice.service.UserService;

import javax.security.auth.login.LoginException;

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
