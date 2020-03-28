package codes.recursive.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("codes.recursive")
public class RecursiveCodesConfig {

    private Boolean useInstancePrincipal;
    private String ociConfigPath;
    private String ociProfile;
    private String metricsCompartmentOcid;
    private String metricsNamespace;
    private String dbOcid;

    public String getDbOcid() {
        return dbOcid;
    }

    public void setDbOcid(String dbOcid) {
        this.dbOcid = dbOcid;
    }

    public String getMetricsNamespace() {
        return metricsNamespace;
    }

    public void setMetricsNamespace(String metricsNamespace) {
        this.metricsNamespace = metricsNamespace;
    }

    public String getMetricsCompartmentOcid() {
        return metricsCompartmentOcid;
    }

    public void setMetricsCompartmentOcid(String metricsCompartmentOcid) {
        this.metricsCompartmentOcid = metricsCompartmentOcid;
    }

    public String getOciConfigPath() {
        return ociConfigPath;
    }

    public void setOciConfigPath(String ociConfigPath) {
        this.ociConfigPath = ociConfigPath;
    }

    public String getOciProfile() {
        return ociProfile;
    }

    public void setOciProfile(String ociProfile) {
        this.ociProfile = ociProfile;
    }

    public Boolean getUseInstancePrincipal() {
        return useInstancePrincipal;
    }

    public void setUseInstancePrincipal(Boolean useInstancePrincipal) {
        this.useInstancePrincipal = useInstancePrincipal;
    }
}
