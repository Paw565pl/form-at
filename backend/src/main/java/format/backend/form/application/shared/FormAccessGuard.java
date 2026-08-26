package format.backend.form.application.shared;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.form.domain.entity.FormEntity;
import format.backend.form.domain.entity.FormStatus;
import format.backend.form.domain.exception.FormNotFoundException;
import format.backend.form.domain.repository.FormRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FormAccessGuard {

    private final FormRepository formRepository;

    public FormEntity verifyAccessAndGetOrThrow(@Nullable UserClaims userClaims, String idOrSlug) {
        val formEntity = formRepository.findByIdOrSlug(idOrSlug).orElseThrow(() -> new FormNotFoundException(idOrSlug));

        val isAuthorOrAdmin = userClaims != null
                && (Objects.equals(formEntity.getAuthorId(), userClaims.id())
                        || userClaims.roles().contains(Role.ADMIN));
        if (formEntity.getStatus() == FormStatus.CLOSED && !isAuthorOrAdmin) throw new FormNotFoundException(idOrSlug);

        return formEntity;
    }
}
