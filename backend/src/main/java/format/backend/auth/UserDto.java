package format.backend.auth;

import java.time.Instant;
import lombok.Builder;

@Builder
public record UserDto(String id, String username, String email, Instant createdAt, Instant updatedAt) {}
