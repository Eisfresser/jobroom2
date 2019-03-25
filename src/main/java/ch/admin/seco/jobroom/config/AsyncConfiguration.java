package ch.admin.seco.jobroom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;

@Configuration
public class AsyncConfiguration {

    static final String EXCEPTION_MESSAGE = "Caught async exception";

    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean
    public TaskDecorator loggingTaskDecorator() {
        return originalRunnable -> new DelegatingSecurityContextRunnable(() -> {
            try {
                originalRunnable.run();
            } catch (Exception e) {
                log.error(EXCEPTION_MESSAGE, e);
            }
        });
    }
}
