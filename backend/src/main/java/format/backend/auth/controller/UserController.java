package format.backend.auth.controller;

import format.backend.auth.dto.UserProfileResponseDto;
import format.backend.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public UserProfileResponseDto findProfileByUsername(@PathVariable String username) {
        return userService.findProfileByUsername(username);
    }
}
