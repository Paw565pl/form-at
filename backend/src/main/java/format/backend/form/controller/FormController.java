package format.backend.form.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;

import format.backend.auth.annotation.IsAuthenticated;
import format.backend.auth.jwt.KeycloakJwtClaimsExtractor;
import format.backend.form.dto.FormDetailResponseDto;
import format.backend.form.dto.FormFilterDto;
import format.backend.form.dto.FormListResponseDto;
import format.backend.form.dto.FormRequestDto;
import format.backend.form.service.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
public class FormController {

    private final KeycloakJwtClaimsExtractor keycloakJwtClaimsExtractor;
    private final FormService formService;

    @GetMapping
    public Page<FormListResponseDto> findAllPublic(
            FormFilterDto filterDto,
            @PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
        return formService.findAllPublic(filterDto, pageable);
    }

    @GetMapping("/{idOrSlug}")
    public FormDetailResponseDto findByIdOrSlug(@PathVariable String idOrSlug) {
        return formService.findByIdOrSlug(idOrSlug);
    }

    @IsAuthenticated
    @PostMapping
    public FormDetailResponseDto create(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody FormRequestDto requestDto) {
        return formService.create(keycloakJwtClaimsExtractor.getClaims(jwt), requestDto);
    }
}
