package format.backend.statistics.application.retrieve;

import format.backend.auth.UserFacade;
import format.backend.form.FormFacade;
import format.backend.submission.SubmissionFacade;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class RetrieveStatisticsHandler {

    private final AsyncTaskExecutor asyncTaskExecutor;

    private final UserFacade userFacade;
    private final FormFacade formFacade;
    private final SubmissionFacade submissionFacade;

    public RetrieveStatisticsHandler(
            @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor,
            UserFacade userFacade,
            FormFacade formFacade,
            SubmissionFacade submissionFacade) {
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.userFacade = userFacade;
        this.formFacade = formFacade;
        this.submissionFacade = submissionFacade;
    }

    public RetrieveStatisticsResponseDto handle() {
        val usersCountFuture = asyncTaskExecutor.submitCompletable(userFacade::count);
        val formsCountFuture = asyncTaskExecutor.submitCompletable(formFacade::count);
        val submissionsCountFuture = asyncTaskExecutor.submitCompletable(submissionFacade::count);

        return RetrieveStatisticsResponseDto.builder()
                .usersCount(usersCountFuture.join())
                .formsCount(formsCountFuture.join())
                .submissionsCount(submissionsCountFuture.join())
                .build();
    }
}
