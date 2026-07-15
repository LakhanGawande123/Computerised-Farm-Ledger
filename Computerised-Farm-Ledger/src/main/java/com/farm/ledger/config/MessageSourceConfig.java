package com.farm.ledger.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    private static final String UTF_8 = "UTF-8";
    private static final String CLASS_PATH_ERROR_CODE_MAPPING = "classpath:/errormapping/httpErrorCodeMapping";
    private static final String CLASS_PATH_ERROR_MESSAGE_MAPPING = "classpath:/errormapping/errorMessageMapping";

    @Bean(name = "errorMessage")
    public MessageSource errorMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(CLASS_PATH_ERROR_MESSAGE_MAPPING);
        messageSource.setDefaultEncoding(UTF_8);
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

    @Bean(name = "httpErrorCode")
    public MessageSource httpErrorCodeSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(CLASS_PATH_ERROR_CODE_MAPPING);
        messageSource.setDefaultEncoding(UTF_8);
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
