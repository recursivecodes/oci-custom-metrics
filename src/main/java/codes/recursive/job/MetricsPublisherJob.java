package codes.recursive.job;

import codes.recursive.service.DBMetricsService;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.SQLException;

@Singleton
public class MetricsPublisherJob {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsPublisherJob.class);

    private final DBMetricsService dbMetricsService;

    public MetricsPublisherJob(DBMetricsService dbMetricsService) {
        this.dbMetricsService = dbMetricsService;
    }

    @Scheduled(fixedDelay = "60s")
    void publishMetricsEverySixtySeconds() throws SQLException {
        LOG.info("Publishing metrics...");
        dbMetricsService.publishMetrics();
        LOG.info("Metrics published!");
    }
}
