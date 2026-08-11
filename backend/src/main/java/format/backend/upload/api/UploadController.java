package format.backend.upload.api;

import dev.paw565pl.auth.IsAuthenticated;
import dev.paw565pl.auth.UserClaims;
import dev.paw565pl.upload.application.upload.BatchUploadRequestDto;
import dev.paw565pl.upload.application.upload.GetBatchUploadPresignedFormDataHandler;
import dev.paw565pl.upload.application.upload.UploadRequestResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
class UploadController {

    private final GetBatchUploadPresignedFormDataHandler getBatchUploadPresignedFormDataHandler;

    @IsAuthenticated
    @PostMapping("/request")
    List<UploadRequestResponseDto> getUploadPresignedFormData(
            @AuthenticationPrincipal UserClaims userClaims, @Valid @RequestBody BatchUploadRequestDto requestDto) {
        return getBatchUploadPresignedFormDataHandler.handle(userClaims, requestDto);
    }
}
