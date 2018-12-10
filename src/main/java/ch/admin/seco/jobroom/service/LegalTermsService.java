package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.LegalTerms;
import ch.admin.seco.jobroom.repository.LegalTermsRepository;
import ch.admin.seco.jobroom.service.dto.LegalTermsDto;
import ch.admin.seco.jobroom.service.mapper.LegalTermsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LegalTermsService {

    private static final Logger LOG = LoggerFactory.getLogger(LegalTermsService.class);

    private final LegalTermsRepository legalTermsRepository;

    private final LegalTermsMapper legalTermsMapper;

    public LegalTermsService(LegalTermsRepository legalTermsRepository, LegalTermsMapper legalTermsMapper) {
        this.legalTermsRepository = legalTermsRepository;
        this.legalTermsMapper = legalTermsMapper;
    }

    public List<LegalTermsDto> findAll() {
        LOG.debug("Request to find all LegalTerms");
        return legalTermsRepository.findAllOrderedByEffectiveAtDesc()
            .stream()
            .map(legalTermsMapper::toDto)
            .collect(toList());
    }

    public LegalTermsDto save(LegalTermsDto dto) throws PastEffectiveLegalTermsException {
        LOG.debug("Request to save LegalTerms : {}", dto);
        LegalTerms entity = legalTermsMapper.toEntity(dto);
        if (entity.isFutureEffective()) {
            LegalTerms savedLegalTerms = legalTermsRepository.save(entity);
            return legalTermsMapper.toDto(savedLegalTerms);
        }
        throw new PastEffectiveLegalTermsException();
    }

    public LegalTermsDto updateIfEffectiveAtIsInFuture(LegalTermsDto dto) throws PastEffectiveLegalTermsException {
        LOG.debug("Request to update LegalTerms : {}", dto);
        LegalTerms entity = legalTermsMapper.toEntity(dto);
        if (entity.isFutureEffective()) {
            LegalTerms savedLegalTerms = legalTermsRepository.save(entity);
            return legalTermsMapper.toDto(savedLegalTerms);
        }
        throw new PastEffectiveLegalTermsException();
    }

    public LegalTermsDto findCurrentLegalTerms() {
        LOG.debug("Request to find current effective LegalTerms");
        List<LegalTerms> effectiveLegalTerms = legalTermsRepository.findPastEffectiveLegalTerms();
        if (effectiveLegalTerms.isEmpty()) {
            throw new CurrentLegalTermsNotFoundException();
        }
        return legalTermsMapper.toDto(effectiveLegalTerms.get(0));
    }

    public void delete(String id) throws IrremovableLegalTermsException {
        LOG.debug("Request to delete LegalTerms : {}", id);
        Optional<LegalTerms> legalTerms = legalTermsRepository.findById(id);
        if (legalTerms.isPresent()) {
            deleteIfEffectiveAtIsInFuture(legalTerms.get());
        }
    }

    private void deleteIfEffectiveAtIsInFuture(LegalTerms legalTerms) throws IrremovableLegalTermsException {
        if (legalTerms.isFutureEffective()) {
            legalTermsRepository.delete(legalTerms);
        } else {
            throw new IrremovableLegalTermsException(legalTerms.getId());
        }
    }
}
