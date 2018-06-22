package ch.admin.seco.jobroom.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.repository.CompanyRepository;
import ch.admin.seco.jobroom.service.dto.CompanyDTO;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Optional<CompanyDTO> findOneByExternalId(String externalId) {
        return companyRepository.findByExternalId(externalId)
            .map(CompanyService::toCompanyDTO);
    }

    private static CompanyDTO toCompanyDTO(Company company) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(company.getId().getValue());
        companyDTO.setExternalId(company.getExternalId());
        companyDTO.setName(company.getName());
        companyDTO.setStreet(company.getStreet());
        companyDTO.setZipCode(company.getZipCode());
        companyDTO.setCity(company.getCity());
        companyDTO.setEmail(company.getEmail());
        companyDTO.setPhone(company.getPhone());
        return companyDTO;
    }
}
