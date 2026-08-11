package format.backend.form.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import format.backend.auth.IsAuthenticated;
import format.backend.auth.UserClaims;
import format.backend.form.application.access.AccessPrivateFormHandler;
import format.backend.form.application.access.AccessPrivateFormRequestDto;
import format.backend.form.application.create.CreateFormHandler;
import format.backend.form.application.delete.DeleteFormHandler;
import format.backend.form.application.list.ListFormsHandler;
import format.backend.form.application.list.ListFormsResponseDto;
import format.backend.form.application.retrieve.RetrieveFormHandler;
import format.backend.form.application.shared.dto.FormRequestDto;
import format.backend.form.application.shared.dto.FormResponseDto;
import format.backend.form.domain.repository.FormListCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
class FormController {

    private final ListFormsHandler listHandler;
    private final RetrieveFormHandler retrieveHandler;
    private final AccessPrivateFormHandler accessPrivateHandler;
    private final CreateFormHandler createHandler;
    private final DeleteFormHandler deleteHandler;

    @GetMapping
    Page<ListFormsResponseDto> list(
            @AuthenticationPrincipal @Nullable UserClaims userClaims, FormListCriteria criteria, Pageable pageable) {
        return listHandler.handle(userClaims, criteria, pageable);
    }

    @GetMapping("/{idOrSlug}")
    FormResponseDto retrieve(@AuthenticationPrincipal @Nullable UserClaims userClaims, @PathVariable String idOrSlug) {
        return retrieveHandler.handle(userClaims, idOrSlug);
    }

    @PostMapping("/{idOrSlug}/access")
    FormResponseDto accessPrivate(
            @AuthenticationPrincipal @Nullable UserClaims userClaims,
            @PathVariable String idOrSlug,
            @Valid @RequestBody AccessPrivateFormRequestDto requestDto) {
        return accessPrivateHandler.handle(userClaims, idOrSlug, requestDto);
    }

    @IsAuthenticated
    @PostMapping
    @ResponseStatus(CREATED)
    FormResponseDto create(
            @AuthenticationPrincipal UserClaims userClaims, @Valid @RequestBody FormRequestDto requestDto) {
        return createHandler.handle(userClaims, requestDto);
    }

    // TODO: update

    @IsAuthenticated
    @DeleteMapping("/{idOrSlug}")
    @ResponseStatus(NO_CONTENT)
    void delete(@AuthenticationPrincipal UserClaims userClaims, @PathVariable String idOrSlug) {
        deleteHandler.handle(userClaims, idOrSlug);
    }
}
