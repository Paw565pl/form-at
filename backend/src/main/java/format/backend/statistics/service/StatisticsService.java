package format.backend.statistics.service;

import format.backend.auth.repository.UserRepository;
import format.backend.form.repository.FormRepository;
import format.backend.statistics.dto.StatisticsResponseDto;
import format.backend.submission.repository.SubmissionRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    @Qualifier("applicationTaskExecutor") private final AsyncTaskExecutor applicationTaskExecutor;

    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final SubmissionRepository submissionRepository;

    public StatisticsResponseDto findStatistics() {
        val usersCountFuture = CompletableFuture.supplyAsync(userRepository::count, applicationTaskExecutor);
        val formsCountFuture = CompletableFuture.supplyAsync(formRepository::count, applicationTaskExecutor);
        val submissionsCountFuture =
                CompletableFuture.supplyAsync(submissionRepository::count, applicationTaskExecutor);

        return new StatisticsResponseDto(
                usersCountFuture.join(), formsCountFuture.join(), submissionsCountFuture.join());
    }
}
