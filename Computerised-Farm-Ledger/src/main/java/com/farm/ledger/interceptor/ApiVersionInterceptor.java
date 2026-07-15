package com.farm.ledger.interceptor;

import com.farm.ledger.config.AppProperties;
import com.farm.ledger.constant.FarmLedgerConstants;
import com.farm.ledger.constant.FarmLedgerExceptionConstants;
import com.farm.ledger.exception.FarmLedgerServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    private final AppProperties appProperties;

    public ApiVersionInterceptor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiVersion = request.getHeader(FarmLedgerConstants.API_VERSION_KEY);
        if (apiVersion == null || apiVersion.trim().isEmpty()) {
            throw new FarmLedgerServiceException(FarmLedgerExceptionConstants.EXCEPTION_API_VERSION_REQUIRED);
        }
        List<String> supported = appProperties.getSupportedApiVersions();
        if (!supported.contains(apiVersion.trim())) {
            String supportedCsv = String.join(",", supported);
            throw new FarmLedgerServiceException(FarmLedgerExceptionConstants.EXCEPTION_API_VERSION_NOT_SUPPORTED, supportedCsv);
        }
        return true;
    }
}
