package ec.edu.uce.library.catalog.domain;

import java.util.Objects;

/**
 * Usage of a Copy ("Uso del ejemplar"): how many times it was returned. Feeds Catalog's usage statistics.
 */
public class CopyUsage {

    private final CopyId copyId;
    private int timesReturned;

    public CopyUsage(CopyId copyId) {
        this(copyId, 0);
    }

    private CopyUsage(CopyId copyId, int timesReturned) {
        this.copyId = Objects.requireNonNull(copyId, "copyId must not be null");
        if (timesReturned < 0) {
            throw new IllegalArgumentException("timesReturned must not be negative");
        }
        this.timesReturned = timesReturned;
    }

    /**
     * Rebuilds a CopyUsage from persistence.
     */
    public static CopyUsage rehydrate(CopyId copyId, int timesReturned) {
        return new CopyUsage(copyId, timesReturned);
    }

    /**
     * Records that the Copy was returned once more.
     */
    public void recordReturn() {
        timesReturned++;
    }

    public CopyId copyId() {
        return copyId;
    }

    public int timesReturned() {
        return timesReturned;
    }
}
