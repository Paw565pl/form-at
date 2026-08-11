package format.backend.core;

import static org.assertj.core.api.Assertions.assertThatCode;

import format.backend.Application;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

final class ModularityTest {

    @Test
    void checkModularity() {
        assertThatCode(() -> ApplicationModules.of(Application.class).verify()).doesNotThrowAnyException();
    }
}
