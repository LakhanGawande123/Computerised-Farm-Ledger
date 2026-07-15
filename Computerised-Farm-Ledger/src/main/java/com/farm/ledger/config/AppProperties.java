package com.farm.ledger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppProperties {

    @Value("${app.supported-api-versions:1.0}")
    private String supportedApiVersions;

    public List<String> getSupportedApiVersions() {
        return Arrays.stream(supportedApiVersions.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
