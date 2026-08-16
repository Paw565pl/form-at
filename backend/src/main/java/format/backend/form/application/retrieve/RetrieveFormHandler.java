package format.backend.form.application.retrieve;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.auth.UserDto;
import format.backend.auth.UserFacade;
import format.backend.core.exception.ForbiddenException;
import format.backend.form.application.shared.FormAccessGuard;
import format.backend.form.application.shared.dto.FormResponseDto;
import format.backend.form.application.shared.mapper.FormMapper;
import format.backend.form.application.shared.mapper.QuestionMapper;
import format.backend.form.domain.entity.FormRatingEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.repository.FormRatingRepository;
import format.backend.upload.UploadFacade;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveFormHandler {

    private final UserFacade userFacade;
    private final UploadFacade uploadFacade;

    private final FormRatingRepository formRatingRepository;
    private final FormMapper formMapper;
    private final QuestionMapper questionMapper;
    private final FormAccessGuard accessGuard;

    public FormResponseDto handle(@Nullable UserClaims userClaims, String idOrSlug) {
        val formEntity = accessGuard.verifyAccessAndGetOrThrow(userClaims, idOrSlug);

        val isAuthorOrAdmin = userClaims != null
                && (Objects.equals(formEntity.getAuthorId(), userClaims.id())
                        || userClaims.roles().contains(Role.ADMIN));
        if (formEntity.getStatus() == FormStatus.PRIVATE && !isAuthorOrAdmin) throw new ForbiddenException();

        val thumbnail = uploadFacade.presignGetUrl(formEntity.getThumbnailKey()).orElse(null);
        val questionResponseDtos = formEntity.getQuestions().stream()
                .map(q -> questionMapper.toResponseDto(
                        q, uploadFacade.presignGetUrl(q.getImageKey()).orElse(null)))
                .toList();
        val userRating = userClaims != null
                ? formRatingRepository
                        .findByFormIdAndAuthorId(Objects.requireNonNull(formEntity.getId()), userClaims.id())
                        .map(FormRatingEntity::getValue)
                        .orElse(null)
                : null;
        val authorName = formEntity.getAuthorId() != null
                ? userFacade
                        .retrieveById(formEntity.getAuthorId())
                        .map(UserDto::username)
                        .orElse(null)
                : null;

        return formMapper.toResponseDto(formEntity, thumbnail, questionResponseDtos, userRating, authorName);
    }
}
