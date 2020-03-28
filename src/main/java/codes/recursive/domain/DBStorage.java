package codes.recursive.domain;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class DBStorage {
    private BigDecimal totalTablespaceSpace;
    private BigDecimal totalSpaceUsed;
    private BigDecimal totalUsedPct;

    public DBStorage() {
    }

    public DBStorage(BigDecimal totalTablespaceSpace, BigDecimal totalSpaceUsed, BigDecimal totalUsedPct) {
        this.totalTablespaceSpace = totalTablespaceSpace;
        this.totalSpaceUsed = totalSpaceUsed;
        this.totalUsedPct = totalUsedPct;
    }

    public BigDecimal getTotalTablespaceSpace() {
        return totalTablespaceSpace;
    }

    public void setTotalTablespaceSpace(BigDecimal totalTablespaceSpace) {
        this.totalTablespaceSpace = totalTablespaceSpace;
    }

    public BigDecimal getTotalSpaceUsed() {
        return totalSpaceUsed;
    }

    public void setTotalSpaceUsed(BigDecimal totalSpaceUsed) {
        this.totalSpaceUsed = totalSpaceUsed;
    }

    public BigDecimal getTotalUsedPct() {
        return totalUsedPct;
    }

    public void setTotalUsedPct(BigDecimal totalUsedPct) {
        this.totalUsedPct = totalUsedPct;
    }
}
