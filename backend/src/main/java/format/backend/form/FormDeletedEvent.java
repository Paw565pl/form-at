package format.backend.form;

import java.util.Set;

public record FormDeletedEvent(String id, Set<String> imageKeys) {
    public FormDeletedEvent {
        imageKeys = Set.copyOf(imageKeys);
    }
}
