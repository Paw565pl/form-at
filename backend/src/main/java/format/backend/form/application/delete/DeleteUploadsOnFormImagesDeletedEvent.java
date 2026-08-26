package format.backend.form.application.delete;

import format.backend.form.FormImagesDeletedEvent;
import format.backend.upload.UploadFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DeleteUploadsOnFormImagesDeletedEvent {

    private final UploadFacade uploadFacade;

    @ApplicationModuleListener
    void on(FormImagesDeletedEvent event) {
        log.debug("Deleting form uploads. event={}", event);

        val deletedCount = uploadFacade.deleteAll(event.removedImageKeys());
        val isSuccess = event.removedImageKeys().size() == deletedCount;

        if (isSuccess) {
            log.debug("Successfully deleted form uploads. event={}", event);
        } else {
            log.warn("Failed to delete form uploads. event={}", event);
            throw new IllegalStateException("Failed to delete form uploads.");
        }
    }
}
