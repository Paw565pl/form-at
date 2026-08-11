package format.backend.statistics.api;

import format.backend.statistics.application.retrieve.RetrieveStatisticsHandler;
import format.backend.statistics.application.retrieve.RetrieveStatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
class StatisticsController {

    private final RetrieveStatisticsHandler retrieveHandler;

    @GetMapping
    RetrieveStatisticsResponseDto retrieve() {
        return retrieveHandler.handle();
    }
}
