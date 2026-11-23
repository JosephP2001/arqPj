package ec.edu.uce.library.catalog.application;

import ec.edu.uce.library.catalog.domain.CopyId;
import ec.edu.uce.library.catalog.domain.CopyUsage;
import ec.edu.uce.library.catalog.domain.CopyUsageRepository;

/**
 * Use case: when Lending reports that a Copy was returned, loads its {@link CopyUsage}
 * (or starts one), calls {@code recordReturn} and saves it.
 */
public class RecordCopyReturn {

    private final CopyUsageRepository usages;

    public RecordCopyReturn(CopyUsageRepository usages) {
        this.usages = usages;
    }

    public CopyUsage record(String copyCode) {
        CopyId copyId = new CopyId(copyCode);
        CopyUsage usage = usages.findById(copyId).orElseGet(() -> new CopyUsage(copyId));
        usage.recordReturn();
        usages.save(usage);
        return usage;
    }
}
