package format.backend.submission.api;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.submission.application.statistics.retrieve.RetrieveSubmissionsStatisticsHandler;
import format.backend.submission.application.statistics.retrieve.RetrieveSubmissionsStatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms/{formIdOrSlug}/submissions")
@RequiredArgsConstructor
class SubmissionsStatisticsController {

    private final RetrieveSubmissionsStatisticsHandler retrieveHandler;

    @IsAuthenticated
    @GetMapping("/statistics")
    RetrieveSubmissionsStatisticsResponseDto retrieve(
            @AuthenticationPrincipal UserClaims userClaims, @PathVariable String formIdOrSlug) {
        return retrieveHandler.handle(userClaims, formIdOrSlug);
    }
}
