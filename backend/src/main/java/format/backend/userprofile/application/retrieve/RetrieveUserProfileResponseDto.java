package format.backend.userprofile.application.retrieve;

import lombok.Builder;

@Builder
public record RetrieveUserProfileResponseDto(String id, String username, Statistics statistics) {
    @Builder
    public record Statistics(long formsCount, long submissionsCount, long commentsCount) {}
}
