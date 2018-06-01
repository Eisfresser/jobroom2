package ch.admin.seco.jobroom.service.mapper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ch.admin.seco.jobroom.service.MailSenderData;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;

public abstract class MailSenderDataMapperDecorator implements MailSenderDataMapper {

    @Autowired
    @Qualifier("delegate")
    private MailSenderDataMapper delegate;

    @Override
    public MailSenderData fromAnonymousContactMessageDto(AnonymousContactMessageDTO anonymousContactMessageDTO) {
        final MailSenderData mailSenderData = delegate.fromAnonymousContactMessageDto(anonymousContactMessageDTO);
        mailSenderData.setContext(getContext(anonymousContactMessageDTO));
        return mailSenderData;
    }

    private Map<String, Object> getContext(AnonymousContactMessageDTO anonymousContactMessage) {
        Map<String, Object> context = new HashMap<>();
        context.put("subject", anonymousContactMessage.getSubject());
        context.put("body", anonymousContactMessage.getBody());
        context.put("phone", anonymousContactMessage.getPhone());
        context.put("email", anonymousContactMessage.getEmail());
        context.put("company", anonymousContactMessage.getCompany());
        return context;
    }
}
