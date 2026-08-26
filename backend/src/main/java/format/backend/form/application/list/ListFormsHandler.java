package format.backend.form.application.list;

import format.backend.auth.UserClaims;
import format.backend.form.domain.repository.FormListCriteria;
import format.backend.form.domain.repository.FormRepository;
import format.backend.upload.UploadFacade;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListFormsHandler {

    private final UploadFacade uploadFacade;

    private final FormRepository formRepository;
    private final ListFormsMapper formMapper;

    public Page<ListFormsResponseDto> handle(
            @Nullable UserClaims userClaims, FormListCriteria criteria, Pageable pageable) {
        val userId = userClaims != null ? userClaims.id() : null;
        return formRepository
                .findAll(userId, criteria, pageable)
                .map(f -> formMapper.toResponseDto(
                        f, uploadFacade.presignGetUrl(f.thumbnailKey()).orElse(null)));
    }
}
