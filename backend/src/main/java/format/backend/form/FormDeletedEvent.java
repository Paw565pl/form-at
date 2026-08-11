package format.backend.form;

import java.util.List;
import lombok.Builder;

@Builder
public record FormDeletedEvent(String id, List<String> imageKeys) {
    public FormDeletedEvent {
        imageKeys = List.copyOf(imageKeys);
    }
}
