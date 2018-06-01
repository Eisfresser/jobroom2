package ch.admin.seco.jobroom.service.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import ch.admin.seco.jobroom.service.MailSenderData;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@DecoratedWith(MailSenderDataMapperDecorator.class)
public interface MailSenderDataMapper {

    MailSenderData fromAnonymousContactMessageDto(AnonymousContactMessageDTO anonymousContactMessageDTO);
}
