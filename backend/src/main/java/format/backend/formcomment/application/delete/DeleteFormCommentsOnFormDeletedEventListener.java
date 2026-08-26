package format.backend.formcomment.application.delete;

import format.backend.form.FormDeletedEvent;
import format.backend.formcomment.domain.repository.FormCommentRatingRepository;
import format.backend.formcomment.domain.repository.FormCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DeleteFormCommentsOnFormDeletedEventListener {

    private final FormCommentRepository formCommentRepository;
    private final FormCommentRatingRepository formCommentRatingRepository;

    @ApplicationModuleListener
    void on(FormDeletedEvent event) {
        log.debug("Deleting form comments. event={}", event);

        val deletedFormCommentsCount = formCommentRepository.deleteAllByFormId(event.id());
        val deletedFormCommentRatingsCount = formCommentRatingRepository.deleteAllByFormId(event.id());

        log.debug(
                "Deleted {} form comments and {} form comment ratings.",
                deletedFormCommentsCount,
                deletedFormCommentRatingsCount);
    }
}
