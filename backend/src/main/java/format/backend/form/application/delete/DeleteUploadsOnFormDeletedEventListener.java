package format.backend.form.application.delete;

import format.backend.form.FormDeletedEvent;
import format.backend.upload.UploadFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class DeleteUploadsOnFormDeletedEventListener {

    private final UploadFacade uploadFacade;

    @ApplicationModuleListener
    void on(FormDeletedEvent event) {
        if (event.imageKeys().isEmpty()) return;
        log.debug("Deleting form uploads. event={}", event);

        val isSuccess = uploadFacade.delete(event.imageKeys());
        if (isSuccess) {
            log.debug("Successfully deleted form uploads. event={}", event);
        } else {
            log.warn(
                    "Deleting {} form uploads failed. event={}",
                    event.imageKeys().size(),
                    event);
            throw new IllegalStateException("Failed to delete uploads.");
        }
    }
}
