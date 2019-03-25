package ch.admin.seco.jobroom.web.rest;


import java.util.Optional;

import io.micrometer.core.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.service.CompanyService;
import ch.admin.seco.jobroom.service.dto.CompanyDTO;

@RestController
@RequestMapping("/api/company")
public class CompanyResource {

    private final CompanyService companyService;

    public CompanyResource(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/find/by-external-id")
    @Timed
    public ResponseEntity<CompanyDTO> findCompanyByExternalId(@RequestParam("id") String id) {
        Optional<CompanyDTO> companyDTO = companyService.findOneByExternalId(id);
        return ResponseUtil.wrapOrNotFound(companyDTO);
    }

}
