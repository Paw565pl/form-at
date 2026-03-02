package format.backend.statistics.controller;

import format.backend.statistics.dto.StatisticsResponseDto;
import format.backend.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public StatisticsResponseDto findStatistics() {
        return statisticsService.findStatistics();
    }
}
