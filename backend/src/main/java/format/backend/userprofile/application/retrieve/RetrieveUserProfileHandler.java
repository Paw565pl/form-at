package format.backend.userprofile.application.retrieve;

import format.backend.auth.UserFacade;
import format.backend.form.FormFacade;
import format.backend.formcomment.FormCommentFacade;
import format.backend.submission.SubmissionFacade;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class RetrieveUserProfileHandler {

    private final AsyncTaskExecutor asyncTaskExecutor;

    private final UserFacade userFacade;
    private final FormFacade formFacade;
    private final SubmissionFacade submissionFacade;
    private final FormCommentFacade formCommentFacade;

    public RetrieveUserProfileHandler(
            @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor,
            UserFacade userFacade,
            FormFacade formFacade,
            SubmissionFacade submissionFacade,
            FormCommentFacade formCommentFacade) {
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.userFacade = userFacade;
        this.formFacade = formFacade;
        this.submissionFacade = submissionFacade;
        this.formCommentFacade = formCommentFacade;
    }

    public RetrieveUserProfileResponseDto handle(String username) {
        val user = userFacade.retrieveByUsername(username).orElseThrow(UserProfileNotFoundException::new);

        val formsCountFuture = asyncTaskExecutor.submitCompletable(() -> formFacade.countByAuthorId(user.id()));
        val submissionsCountFuture =
                asyncTaskExecutor.submitCompletable(() -> submissionFacade.countByAuthorId(user.id()));
        val formCommentsFuture =
                asyncTaskExecutor.submitCompletable(() -> formCommentFacade.countByAuthorId(user.id()));

        return RetrieveUserProfileResponseDto.builder()
                .id(user.id())
                .username(user.username())
                .statistics(RetrieveUserProfileResponseDto.Statistics.builder()
                        .formsCount(formsCountFuture.join())
                        .submissionsCount(submissionsCountFuture.join())
                        .commentsCount(formCommentsFuture.join())
                        .build())
                .build();
    }
}
