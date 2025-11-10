package format.backend.submission.service;

import format.backend.auth.jwt.KeycloakJwtClaims;
import format.backend.form.service.FormService;
import format.backend.submission.dto.SubmissionResponseDto;
import format.backend.submission.exception.SubmissionNotFoundForUserException;
import format.backend.submission.mapper.SubmissionMapper;
import format.backend.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final FormService formService;

    public Page<SubmissionResponseDto> findAllByFormIdOrSlug(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug, Pageable pageable) {
        val form = formService.findOrThrow(formIdOrSlug);
        if (!form.getAuthor().getId().equals(keycloakJwtClaims.sub()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        val submissions = submissionRepository.findAllByFormId(
                form.getId(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("_id"))));

        return submissions.map(submissionMapper::toResponseDto);
    }

    public SubmissionResponseDto findByFormIdOrSlugAndAuthorId(
            KeycloakJwtClaims keycloakJwtClaims, String formIdOrSlug) {
        val form = formService.findOrThrow(formIdOrSlug);
        val submission = submissionRepository
                .findByFormIdAndAuthorId(form.getId(), keycloakJwtClaims.sub())
                .orElseThrow(() -> new SubmissionNotFoundForUserException(formIdOrSlug));

        return submissionMapper.toResponseDto(submission);
    }
}
