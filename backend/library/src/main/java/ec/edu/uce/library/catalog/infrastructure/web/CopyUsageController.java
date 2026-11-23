package ec.edu.uce.library.catalog.infrastructure.web;

import ec.edu.uce.library.catalog.domain.CopyId;
import ec.edu.uce.library.catalog.domain.CopyUsage;
import ec.edu.uce.library.catalog.domain.CopyUsageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only view of Catalog's usage statistics, to show the CopyReturned event arriving in another context.
 */
@RestController
@RequestMapping("/api/copy-usages")
class CopyUsageController {

    private final CopyUsageRepository usages;

    CopyUsageController(CopyUsageRepository usages) {
        this.usages = usages;
    }

    @GetMapping("/{copyCode}")
    CopyUsageResponse usage(@PathVariable String copyCode) {
        CopyId copyId = new CopyId(copyCode);
        CopyUsage usage = usages.findById(copyId).orElseGet(() -> new CopyUsage(copyId));
        return new CopyUsageResponse(usage.copyId().code(), usage.timesReturned());
    }

    record CopyUsageResponse(String copyCode, int timesReturned) {
    }
}
