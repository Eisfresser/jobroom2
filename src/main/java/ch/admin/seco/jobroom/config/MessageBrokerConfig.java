package ch.admin.seco.jobroom.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.NullChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MessageBrokerConfig {

    @Configuration
    @Profile("!messagebroker-mock")
    @EnableBinding(Source.class)
    static class DefaultMessageBroker { }

    @Configuration
    @Profile("messagebroker-mock")
    static class MockedMessageBroker {

        @Bean
        Source source() {
            return new MockedSource();
        }

        static class MockedSource implements Source {
            @Override
            public MessageChannel output() {
                return new NullChannel();
            }
        }

    }
}
