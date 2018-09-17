package ch.admin.seco.jobroom.service.impl.messaging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.RegistrationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CandidateDeleteEventConsumerTest {

    @Autowired
    private Sink sink;

    @MockBean
    private UserInfoRepository userInfoRepository;

    @MockBean
    private MailService mailService;

    @MockBean
    private UserInfo userInfo;

    @MockBean
    private RegistrationService registrationService;

    @Autowired
    private StesUnregisteringProperties stesUnregisteringProperties;

    @Before
    public void setUp() {
        given(userInfo.getEmail()).willReturn("stes@email.ch");
    }

    @Test
    public void shouldOnCandidateDeleteEventSendEmailForUnregisterCandidate() throws UserNotFoundException {
        //given
        given(userInfoRepository.findByPersonNumber(Long.MAX_VALUE)).willReturn(Optional.of(userInfo));

        //when
        this.sink.input().send(MessageBuilder.withPayload(CandidateDeletedEvent.from(UUID.randomUUID(), Long.MAX_VALUE)).build());

        //then
        verify(this.mailService, times(1)).sendStesUnregisteringMail(any(), any());
        verify(this.registrationService, never()).unregisterJobSeeker(anyString());
    }

    @Test
    public void shouldOnCandidateDeleteEventNotSendEmailForUnregisterCandidateNorUnregisterCandidate() throws UserNotFoundException {
        //given
        given(userInfoRepository.findByPersonNumber(Long.MAX_VALUE)).willReturn(Optional.empty());

        //when
        this.sink.input().send(MessageBuilder.withPayload(CandidateDeletedEvent.from(UUID.randomUUID(), Long.MAX_VALUE)).build());

        //then
        verify(this.mailService, never()).sendStesUnregisteringMail(any(), any());
        verify(this.registrationService, never()).unregisterJobSeeker(any());
    }

    @Test
    public void shouldOnCandidateDeleteEventUnregisterCandidate() throws UserNotFoundException {
        //given
        this.stesUnregisteringProperties.setAutoUnregisteringEnabled(true);
        given(userInfoRepository.findByPersonNumber(Long.MAX_VALUE)).willReturn(Optional.of(userInfo));

        //when
        this.sink.input().send(MessageBuilder.withPayload(CandidateDeletedEvent.from(UUID.randomUUID(), Long.MAX_VALUE)).build());

        //then
        verify(this.mailService, never()).sendStesUnregisteringMail(any(), any());
        verify(this.registrationService, only()).unregisterJobSeeker(any());
    }
}
