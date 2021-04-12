package com.doesitwork.springboot.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.doesitwork.springboot.exception.UnauthorizedException;
import com.doesitwork.springboot.util.HttpRequestUtil;
import com.doesitwork.springboot.validation.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EndpointFilter extends OncePerRequestFilter {
    private static final String OPERATIONS_ENDPOINT = "/operations";

    @Autowired
    private ObjectMapper mapper;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${applicationConfig.apiKey}")
    private String operationsApiKey;

    @Value("${applicationConfig.environment}")
    private String environment;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,
                                                                                                                       IOException {

        final String requestUri = request.getRequestURI();

        final String apiKey = HttpRequestUtil.getApiKey(request);

        if (requestUri.startsWith(OPERATIONS_ENDPOINT) && !requestUri.endsWith(".raml") && requestUri.indexOf(".") == -1) {
            try {
                if (!operationsApiKey.equals(apiKey)) {
                    throw UnauthorizedException.instance("invalid_api_key");
                }

                super.doFilter(request, response, filterChain);
                return;

            } catch (UnauthorizedException e) {
                buildUnauthorizedResponse(response);
                return;

            } catch (Exception e) {
                buildErrorResponse(response);
                return;

            }
        } else {
            super.doFilter(request, response, filterChain);
        }
    }

    private void buildUnauthorizedResponse(HttpServletResponse response) {
        try {
            ErrorResponse errorResponse = ErrorResponse.instance("invalid_api_credentials", HttpStatus.UNAUTHORIZED.value());
            response.getWriter().print(mapper.writeValueAsString(errorResponse));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildErrorResponse(HttpServletResponse response) {
        try {
            ErrorResponse errorResponse = ErrorResponse.instance("access_rate_internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().print(mapper.writeValueAsString(errorResponse));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
