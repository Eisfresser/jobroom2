package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.LegalTerms;
import ch.admin.seco.jobroom.security.IsAdmin;
import ch.admin.seco.jobroom.service.IrremovableLegalTermsException;
import ch.admin.seco.jobroom.service.LegalTermsService;
import ch.admin.seco.jobroom.service.PastEffectiveLegalTermsException;
import ch.admin.seco.jobroom.service.dto.LegalTermsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static ch.admin.seco.jobroom.web.rest.util.HeaderUtil.*;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.ResponseEntity.*;

/**
 * REST controller for managing legal terms entries.
 */
@RestController
@RequestMapping("/api/legal-terms")
public class LegalTermsResource {

    private final Logger LOG = LoggerFactory.getLogger(LegalTermsResource.class);

    private final LegalTermsService legalTermsService;

    public LegalTermsResource(LegalTermsService legalTermsService) {
        this.legalTermsService = legalTermsService;
    }

    @GetMapping("/current")
    public ResponseEntity<LegalTermsDto> findCurrentLegalTerms() {
        LOG.debug("REST request to find current legal terms");
        LegalTermsDto currentLegalTerms = this.legalTermsService.findCurrentLegalTerms();
        return ok().body(currentLegalTerms);
    }

    @IsAdmin
    @GetMapping
    public ResponseEntity<List<LegalTermsDto>> findAllLegalTerms() {
        LOG.debug("REST request to find all legal terms entries");
        List<LegalTermsDto> allLegalTerms = this.legalTermsService.findAll();
        return ok().body(allLegalTerms);
    }

    @IsAdmin
    @PostMapping
    public ResponseEntity<LegalTermsDto> create(@Valid @RequestBody LegalTermsDto dto) throws URISyntaxException {
        LOG.debug("REST request to create a new legal terms entry");
        if (dto.getId() != null) {
            badRequest()
                .headers(createFailureAlert(LegalTerms.class.getName(), "idexists", "A new LegalTerms cannot already have an ID"))
                .build();
        }
        try {
            LegalTermsDto result = this.legalTermsService.save(dto);
            return created(new URI("/api/legal-terms/" + result.getId()))
                .headers(createEntityCreationAlert(LegalTerms.class.getName(), result.getId()))
                .body(result);
        } catch (PastEffectiveLegalTermsException e) {
            return status(NOT_ACCEPTABLE)
                .headers(createFailureAlert(LegalTerms.class.getName(), e.getClass().getName(), e.getMessage()))
                .build();
        }
    }

    @IsAdmin
    @PutMapping("/{id}")
    public ResponseEntity<LegalTermsDto> update(@PathVariable String id, @Valid @RequestBody LegalTermsDto dto) {
        LOG.debug("REST request to update a legal terms entry");
        try {
            if (Objects.equals(id, dto.getId())) {
                badRequest()
                    .headers(createFailureAlert(
                        LegalTerms.class.getName(),
                        "idmismatch",
                        "An id parameter does not match the ID of DTO"))
                    .build();
            }
            LegalTermsDto result = this.legalTermsService.updateIfEffectiveAtIsInFuture(dto);
            return ok()
                .headers(createEntityUpdateAlert(LegalTerms.class.getName(), result.getId()))
                .body(result);
        } catch (PastEffectiveLegalTermsException e) {
            return status(NOT_ACCEPTABLE)
                .headers(createFailureAlert(LegalTerms.class.getName(), e.getClass().getName(), e.getMessage()))
                .build();
        }
    }

    @IsAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        LOG.debug("REST request to delete a legal terms entry : {}", id);
        try {
            this.legalTermsService.delete(id);
            return ok()
                .headers(createEntityDeletionAlert(LegalTerms.class.getName(), id)).build();
        } catch (IrremovableLegalTermsException e) {
            return status(NOT_ACCEPTABLE)
                .headers(createFailureAlert(LegalTerms.class.getName(), e.getClass().getName(), e.getMessage()))
                .build();
        }
    }
}
