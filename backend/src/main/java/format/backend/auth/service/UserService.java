package format.backend.auth.service;

import format.backend.auth.dto.UserProfile;
import format.backend.auth.dto.UserStatistics;
import format.backend.auth.entity.UserEntity;
import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.auth.repository.UserRepository;
import format.backend.comment.repository.CommentRepository;
import format.backend.form.repository.FormRepository;
import format.backend.submission.repository.SubmissionRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("applicationTaskExecutor") private final AsyncTaskExecutor applicationTaskExecutor;

    private final UserRepository userRepository;
    private final FormRepository formRepository;
    private final SubmissionRepository submissionRepository;
    private final CommentRepository commentRepository;

    public UserEntity findOrThrow(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public UserProfile findProfileByUsername(String username) {
        val user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        val formsCountFuture = CompletableFuture.supplyAsync(
                () -> formRepository.countAllByAuthorId(user.getId()), applicationTaskExecutor);
        val submissionsCountFuture = CompletableFuture.supplyAsync(
                () -> submissionRepository.countAllByAuthorId(user.getId()), applicationTaskExecutor);
        val commentsCountFuture = CompletableFuture.supplyAsync(
                () -> commentRepository.countAllByAuthorId(user.getId()), applicationTaskExecutor);

        val statistics =
                new UserStatistics(formsCountFuture.join(), submissionsCountFuture.join(), commentsCountFuture.join());

        return new UserProfile(user.getId(), user.getUsername(), statistics);
    }

    @Transactional
    public UserEntity createOrUpdate(KeycloakJwtClaims jwtClaims) {
        return userRepository.save(new UserEntity(jwtClaims.sub(), jwtClaims.username(), jwtClaims.email()));
    }
}
