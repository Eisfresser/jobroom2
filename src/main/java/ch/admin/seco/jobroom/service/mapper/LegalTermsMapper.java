package ch.admin.seco.jobroom.service.mapper;

import ch.admin.seco.jobroom.domain.LegalTerms;
import ch.admin.seco.jobroom.service.dto.LegalTermsDto;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface LegalTermsMapper extends EntityMapper<LegalTermsDto, LegalTerms> {
}
