package format.backend.submission.application.shared;

import format.backend.auth.Role;
import format.backend.auth.UserClaims;
import format.backend.core.exception.ForbiddenException;
import format.backend.form.FormFacade;
import format.backend.form.FormView;
import format.backend.submission.domain.exception.SubmissionOperationNotSupported;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionAccessGuard {

    private final FormFacade formFacade;

    public FormView verifyOwnAccessAndGetOrThrow(UserClaims userClaims, String formIdOrSlug) {
        val formView = formFacade.getViewOrThrow(userClaims, formIdOrSlug);
        if (!formView.saveSubmissions() || formView.authorId() == null) {
            throw new SubmissionOperationNotSupported(formIdOrSlug);
        }

        return formView;
    }

    public FormView verifyAccessAndGetOrThrow(UserClaims userClaims, String formIdOrSlug) {
        val formView = verifyOwnAccessAndGetOrThrow(userClaims, formIdOrSlug);

        val isAuthorOrAdmin = Objects.equals(formView.authorId(), userClaims.id())
                || userClaims.roles().contains(Role.ADMIN);
        if (!isAuthorOrAdmin) throw new ForbiddenException();

        return formView;
    }
}
