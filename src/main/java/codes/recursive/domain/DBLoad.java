package codes.recursive.domain;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class DBLoad {
    private BigDecimal executions;
    private BigDecimal userCalls;
    private BigDecimal parses;
    private BigDecimal commits;
    private BigDecimal rollbacks;
    private BigDecimal logons;
    private BigDecimal totalPhysicalReads;
    private BigDecimal totalPhysicalWrites;
    private BigDecimal phyReadTotalIOReqs;
    private BigDecimal phyWriteTotalIOReqs;
    private BigDecimal dbCpu;
    private BigDecimal dbTime;
    private BigDecimal userIOTime;

    public DBLoad() {
    }

    public DBLoad(BigDecimal executions, BigDecimal userCalls, BigDecimal parses, BigDecimal commits, BigDecimal rollbacks, BigDecimal logons, BigDecimal totalPhysicalReads, BigDecimal totalPhysicalWrites, BigDecimal phyReadTotalIOReqs, BigDecimal phyWriteTotalIOReqs, BigDecimal dbCpu, BigDecimal dbTime, BigDecimal userIOTime) {
        this.executions = executions;
        this.userCalls = userCalls;
        this.parses = parses;
        this.commits = commits;
        this.rollbacks = rollbacks;
        this.logons = logons;
        this.totalPhysicalReads = totalPhysicalReads;
        this.totalPhysicalWrites = totalPhysicalWrites;
        this.phyReadTotalIOReqs = phyReadTotalIOReqs;
        this.phyWriteTotalIOReqs = phyWriteTotalIOReqs;
        this.dbCpu = dbCpu;
        this.dbTime = dbTime;
        this.userIOTime = userIOTime;
    }

    public BigDecimal getExecutions() {
        return executions;
    }

    public void setExecutions(BigDecimal executions) {
        this.executions = executions;
    }

    public BigDecimal getUserCalls() {
        return userCalls;
    }

    public void setUserCalls(BigDecimal userCalls) {
        this.userCalls = userCalls;
    }

    public BigDecimal getParses() {
        return parses;
    }

    public void setParses(BigDecimal parses) {
        this.parses = parses;
    }

    public BigDecimal getCommits() {
        return commits;
    }

    public void setCommits(BigDecimal commits) {
        this.commits = commits;
    }

    public BigDecimal getRollbacks() {
        return rollbacks;
    }

    public void setRollbacks(BigDecimal rollbacks) {
        this.rollbacks = rollbacks;
    }

    public BigDecimal getLogons() {
        return logons;
    }

    public void setLogons(BigDecimal logons) {
        this.logons = logons;
    }

    public BigDecimal getTotalPhysicalReads() {
        return totalPhysicalReads;
    }

    public void setTotalPhysicalReads(BigDecimal totalPhysicalReads) {
        this.totalPhysicalReads = totalPhysicalReads;
    }

    public BigDecimal getTotalPhysicalWrites() {
        return totalPhysicalWrites;
    }

    public void setTotalPhysicalWrites(BigDecimal totalPhysicalWrites) {
        this.totalPhysicalWrites = totalPhysicalWrites;
    }

    public BigDecimal getPhyReadTotalIOReqs() {
        return phyReadTotalIOReqs;
    }

    public void setPhyReadTotalIOReqs(BigDecimal phyReadTotalIOReqs) {
        this.phyReadTotalIOReqs = phyReadTotalIOReqs;
    }

    public BigDecimal getPhyWriteTotalIOReqs() {
        return phyWriteTotalIOReqs;
    }

    public void setPhyWriteTotalIOReqs(BigDecimal phyWriteTotalIOReqs) {
        this.phyWriteTotalIOReqs = phyWriteTotalIOReqs;
    }

    public BigDecimal getDbCpu() {
        return dbCpu;
    }

    public void setDbCpu(BigDecimal dbCpu) {
        this.dbCpu = dbCpu;
    }

    public BigDecimal getDbTime() {
        return dbTime;
    }

    public void setDbTime(BigDecimal dbTime) {
        this.dbTime = dbTime;
    }

    public BigDecimal getUserIOTime() {
        return userIOTime;
    }

    public void setUserIOTime(BigDecimal userIOTime) {
        this.userIOTime = userIOTime;
    }
}
