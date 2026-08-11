package format.backend.userprofile.api;

import format.backend.userprofile.application.retrieve.RetrieveUserProfileHandler;
import format.backend.userprofile.application.retrieve.RetrieveUserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserProfileController {

    private final RetrieveUserProfileHandler retrieveHandler;

    @GetMapping("/{username}")
    RetrieveUserProfileResponseDto retrieve(@PathVariable String username) {
        return retrieveHandler.handle(username);
    }
}
