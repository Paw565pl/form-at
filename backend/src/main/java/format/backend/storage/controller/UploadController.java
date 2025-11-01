package format.backend.storage.controller;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.storage.dto.UploadRequestDto;
import format.backend.storage.dto.UploadRequestResponseDto;
import format.backend.storage.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;
    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;

    @IsAuthenticated
    @PostMapping("/request")
    public UploadRequestResponseDto getUploadPresignedFormData(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UploadRequestDto requestDto) {
        return uploadService.getUploadPresignedFormData(keycloakJwtClaimsExtractor.getClaims(jwt), requestDto);
    }
}
