package codes.recursive.service;

import codes.recursive.config.RecursiveCodesConfig;
import codes.recursive.domain.DBLoad;
import codes.recursive.domain.DBStorage;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import com.oracle.bmc.monitoring.responses.PostMetricDataResponse;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Singleton
public class DBMetricsService {

    private MonitoringClient monitoringClient;
    private BasicAuthenticationDetailsProvider provider;
    private final RecursiveCodesConfig config;
    private final DataSource dataSource;

    public DBMetricsService(RecursiveCodesConfig config, DataSource dataSource ) throws IOException, SQLException {
        this.config = config;
        this.dataSource = dataSource;
        if( config.getUseInstancePrincipal() ) {
            provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        }
        else {
            provider = new ConfigFileAuthenticationDetailsProvider(config.getOciConfigPath(), config.getOciProfile());
        }
        monitoringClient = MonitoringClient.builder().endpoint("https://telemetry-ingestion.us-phoenix-1.oraclecloud.com").build(provider);
    }

    public void publishMetrics() throws SQLException {
        DBLoad dbLoad = getDBLoad();
        DBStorage dbStorage = getDBStorage();
        List<MetricDataDetails> metricDataDetailsList = new ArrayList<>();

        BeanIntrospection<DBLoad> dbLoadBeanIntrospection = BeanIntrospection.getIntrospection(DBLoad.class);
        BeanIntrospection<DBStorage> dbStorageBeanIntrospection = BeanIntrospection.getIntrospection(DBStorage.class);

        for (String loadPropertyName : dbLoadBeanIntrospection.getPropertyNames()) {
            BeanProperty<DBLoad, BigDecimal> loadProp = dbLoadBeanIntrospection.getRequiredProperty(loadPropertyName, BigDecimal.class);
            BigDecimal currentValue = loadProp.get(dbLoad);

            Datapoint loadDp = Datapoint.builder()
                    .value(currentValue.doubleValue())
                    .timestamp(new Date())
                    .build();

            MetricDataDetails loadMetricDataDetails = MetricDataDetails.builder()
                    .compartmentId(config.getMetricsCompartmentOcid())
                    .namespace(config.getMetricsNamespace())
                    .dimensions(Map.of(
                            "dbId", config.getDbOcid()
                    ))
                    .resourceGroup("db-load")
                    .name(loadPropertyName)
                    .datapoints(List.of(loadDp))
                    .build();

            metricDataDetailsList.add(loadMetricDataDetails);
        }

        for (String storagePropertyName : dbStorageBeanIntrospection.getPropertyNames()) {
            BeanProperty<DBStorage, BigDecimal> storageProp = dbStorageBeanIntrospection.getRequiredProperty(storagePropertyName, BigDecimal.class);
            BigDecimal currentValue = storageProp.get(dbStorage);

            Datapoint storageDp = Datapoint.builder()
                    .value(currentValue.doubleValue())
                    .timestamp(new Date())
                    .build();

            MetricDataDetails storageMetricDataDetails = MetricDataDetails.builder()
                    .compartmentId(config.getMetricsCompartmentOcid())
                    .namespace(config.getMetricsNamespace())
                    .dimensions(Map.of(
                            "dbId", config.getDbOcid()
                    ))
                    .resourceGroup("db-storage")
                    .name(storagePropertyName)
                    .datapoints(List.of(storageDp))
                    .build();

            metricDataDetailsList.add(storageMetricDataDetails);
        }

        PostMetricDataDetails postMetricDataDetails = PostMetricDataDetails.builder().metricData(metricDataDetailsList).build();
        PostMetricDataRequest postMetricDataRequest = PostMetricDataRequest.builder()
                .postMetricDataDetails(postMetricDataDetails)
                .build();
        PostMetricDataResponse postMetricDataResponse = monitoringClient.postMetricData(postMetricDataRequest);
    }

    public DBLoad getDBLoad() throws SQLException {
        DBLoad dbLoad = new DBLoad();

        try (Connection connection = dataSource.getConnection()) {
            String qry = "-- VIEWS USED\n" +
                    "-- v$sysstat,\n" +
                    "-- v$sys_time_model,\n" +
                    "-- v$system_wait_class,\n" +
                    "WITH rdb_load AS (\n" +
                    "    SELECT\n" +
                    "        inst_id,\n" +
                    "        executions,\n" +
                    "        usercalls,\n" +
                    "        parses,\n" +
                    "        commits,\n" +
                    "        rollbacks,\n" +
                    "        logons,\n" +
                    "        totalphysicalreads,\n" +
                    "        totalphysicalwrites,\n" +
                    "        phyreadtotalioreqs,\n" +
                    "        phywritetotalioreqs\n" +
                    "    FROM\n" +
                    "        TABLE ( gv$(CURSOR(\n" +
                    "            SELECT\n" +
                    "                to_number(userenv('INSTANCE')) AS inst_id, SUM(decode(name, 'execute count', value, 0)) executions, SUM(decode(name\n" +
                    "                , 'user calls', value, 0)) usercalls, SUM(decode(name, 'parse count (total)', value, 0)) parses, SUM(decode(name,\n" +
                    "                'user commits', value, 0)) commits, SUM(decode(name, 'user rollbacks', value, 0)) rollbacks, SUM(decode(name, 'logons cumulative'\n" +
                    "                , value, 0)) logons, SUM(decode(name, 'physical read total bytes', value, 0)) totalphysicalreads, SUM(decode(name\n" +
                    "                , 'physical write total bytes', value, 0)) totalphysicalwrites, SUM(decode(name, 'physical read total IO requests'\n" +
                    "                , value, 0)) phyreadtotalioreqs, SUM(decode(name, 'physical write total IO requests', value, 0)) phywritetotalioreqs\n" +
                    "            FROM\n" +
                    "                v$sysstat\n" +
                    "            WHERE\n" +
                    "                con_id = 0\n" +
                    "            GROUP BY\n" +
                    "                to_number(userenv('INSTANCE'))\n" +
                    "        )) )\n" +
                    "), rdb_time AS (\n" +
                    "    SELECT\n" +
                    "        inst_id,\n" +
                    "        dbcpu,\n" +
                    "        dbtime\n" +
                    "    FROM\n" +
                    "        TABLE ( gv$(CURSOR(\n" +
                    "            SELECT\n" +
                    "                to_number(userenv('INSTANCE')) AS inst_id, SUM(decode(stat_name, 'DB CPU', value / 10000, 0)) dbcpu, SUM(decode(stat_name\n" +
                    "                , 'DB time', value / 1000000, 0)) dbtime\n" +
                    "            FROM\n" +
                    "                v$sys_time_model\n" +
                    "            WHERE\n" +
                    "                con_id = 0\n" +
                    "            GROUP BY\n" +
                    "                to_number(userenv('INSTANCE'))\n" +
                    "        )) )\n" +
                    "), user_io AS (\n" +
                    "    SELECT\n" +
                    "        inst_id,\n" +
                    "        useriotime\n" +
                    "    FROM\n" +
                    "        TABLE ( gv$(CURSOR(\n" +
                    "            SELECT\n" +
                    "                to_number(userenv('INSTANCE')) AS inst_id, time_waited_fg / 100 AS useriotime\n" +
                    "            FROM\n" +
                    "                v$system_wait_class\n" +
                    "            WHERE\n" +
                    "                wait_class = 'User I/O'\n" +
                    "                AND con_id = 0\n" +
                    "        )) )\n" +
                    ")\n" +
                    "SELECT\n" +
                    "    sum(rdb_load.executions) as executions,\n" +
                    "    sum(rdb_load.usercalls) as usercalls,\n" +
                    "    sum(rdb_load.parses) as parses,\n" +
                    "    sum(rdb_load.commits) as commits,\n" +
                    "    sum(rdb_load.rollbacks) as rollbacks,\n" +
                    "    sum(rdb_load.logons) as logons,\n" +
                    "    sum(rdb_load.totalphysicalreads) as totalphysicalreads,\n" +
                    "    sum(rdb_load.totalphysicalwrites) as totalphysicalwrites,\n" +
                    "    sum(rdb_load.phyreadtotalioreqs) as phyreadtotalioreqs,\n" +
                    "    sum(rdb_load.phywritetotalioreqs) as phywritetotalioreqs,\n" +
                    "    sum(rdb_time.dbcpu) as dbcpu,\n" +
                    "    sum(rdb_time.dbtime) as dbtime,\n" +
                    "    sum(user_io.useriotime) as useriotime\n" +
                    "FROM\n" +
                    "    rdb_load,\n" +
                    "    rdb_time,\n" +
                    "    user_io\n" +
                    "WHERE\n" +
                    "    rdb_load.inst_id = rdb_time.inst_id\n" +
                    "    AND rdb_time.inst_id = user_io.inst_id";
            ResultSet resultSet = connection.createStatement().executeQuery(qry);
            resultSet.next();
            dbLoad.setExecutions(resultSet.getBigDecimal("executions"));
            dbLoad.setUserCalls(resultSet.getBigDecimal("usercalls"));
            dbLoad.setParses(resultSet.getBigDecimal("parses"));
            dbLoad.setCommits(resultSet.getBigDecimal("commits"));
            dbLoad.setRollbacks(resultSet.getBigDecimal("rollbacks"));
            dbLoad.setLogons(resultSet.getBigDecimal("logons"));
            dbLoad.setTotalPhysicalReads(resultSet.getBigDecimal("totalphysicalreads"));
            dbLoad.setTotalPhysicalWrites(resultSet.getBigDecimal("totalphysicalwrites"));
            dbLoad.setPhyReadTotalIOReqs(resultSet.getBigDecimal("phyreadtotalioreqs"));
            dbLoad.setPhyWriteTotalIOReqs(resultSet.getBigDecimal("phywritetotalioreqs"));
            dbLoad.setDbCpu(resultSet.getBigDecimal("dbcpu"));
            dbLoad.setDbTime(resultSet.getBigDecimal("dbtime"));
            dbLoad.setUserIOTime(resultSet.getBigDecimal("useriotime"));
        }
        return dbLoad;
    }

    public DBStorage getDBStorage() throws SQLException {
        DBStorage dbStorage = new DBStorage();

        try (Connection connection = dataSource.getConnection()) {
            String qry = "-- VIEWS\n" +
                    "-- cdb_tablespaces\n" +
                    "-- cdb_tablespace_usage_metrics\n" +
                    "WITH tbsp_stats AS (\n" +
                    "    SELECT\n" +
                    "        SUM(tablespace_space) AS total_tablespace_space,\n" +
                    "        SUM(space_used) AS total_space_used\n" +
                    "    FROM\n" +
                    "        (\n" +
                    "            SELECT\n" +
                    "                m.tablespace_name,\n" +
                    "                t.contents,\n" +
                    "                MAX(round((m.tablespace_size) * t.block_size / 1024 / 1024 / 1024, 9)) tablespace_space,\n" +
                    "                MAX(round((m.used_space) * t.block_size / 1024 / 1024 / 1024, 9)) space_used,\n" +
                    "                MAX(round(m.used_percent, 2)) used_pct\n" +
                    "            FROM\n" +
                    "                cdb_tablespace_usage_metrics   m,\n" +
                    "                cdb_tablespaces                t\n" +
                    "            WHERE\n" +
                    "                t.tablespace_name = m.tablespace_name\n" +
                    "            GROUP BY\n" +
                    "                m.tablespace_name,\n" +
                    "                t.contents\n" +
                    "        )\n" +
                    ")\n" +
                    "SELECT\n" +
                    "    tbsp_stats.total_tablespace_space,\n" +
                    "    tbsp_stats.total_space_used,\n" +
                    "    tbsp_stats.total_space_used / tbsp_stats.total_tablespace_space AS total_used_pct\n" +
                    "FROM\n" +
                    "    tbsp_stats";
            ResultSet resultSet = connection.createStatement().executeQuery(qry);
            resultSet.next();
            dbStorage.setTotalTablespaceSpace(resultSet.getBigDecimal("total_tablespace_space"));
            dbStorage.setTotalSpaceUsed(resultSet.getBigDecimal("total_space_used"));
            dbStorage.setTotalUsedPct(resultSet.getBigDecimal("total_used_pct"));
        }
        return dbStorage;
    }

}
