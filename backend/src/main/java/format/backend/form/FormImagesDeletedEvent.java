package format.backend.form;

import java.util.Set;

public record FormImagesDeletedEvent(String id, Set<String> removedImageKeys) {
    public FormImagesDeletedEvent {
        removedImageKeys = Set.copyOf(removedImageKeys);
    }
}
