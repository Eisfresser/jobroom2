package ch.admin.seco.jobroom.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ch.admin.seco.jobroom.security.registration.stes.StesService;

/**
 * This config exists only to make us able to mock the StesService feign client
 * by switching EnableFeignClient on/off via Spring profile.
 */
@Configuration
@EnableFeignClients(basePackageClasses = StesService.class)
@Profile("!stes-mock")
public class StesConfig {
}
