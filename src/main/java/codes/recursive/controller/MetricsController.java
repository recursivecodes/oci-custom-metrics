package codes.recursive.controller;

import codes.recursive.domain.DBLoad;
import codes.recursive.domain.DBStorage;
import codes.recursive.service.DBMetricsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.sql.SQLException;
import java.util.Map;

@Controller("/metrics")
public class MetricsController {

    private final DBMetricsService dbMetricsService;

    public MetricsController(DBMetricsService dbMetricsService) {
        this.dbMetricsService = dbMetricsService;
    }

    @Get("/all")
    public HttpResponse getAll() throws SQLException {
        DBLoad dbLoad = dbMetricsService.getDBLoad();
        DBStorage dbStorage = dbMetricsService.getDBStorage();
        return HttpResponse.ok(
                Map.of(
                    "load", dbLoad,
                    "storage", dbStorage
                )
        );
    }

    @Get("/test-publish")
    public HttpResponse testPublish() throws SQLException {
        dbMetricsService.publishMetrics();
        return HttpResponse.ok();
    }
}