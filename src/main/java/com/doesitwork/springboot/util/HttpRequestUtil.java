package com.doesitwork.springboot.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class HttpRequestUtil {

    public static String getOriginIp(HttpServletRequest request) {
        String remoteAddress = null;

        if (Objects.nonNull(request)) {
            remoteAddress = request.getHeader("X-FORWARDED-FOR");
            if (StringUtils.isEmpty(remoteAddress)) {
                remoteAddress = request.getRemoteAddr();
            }
        }

        return remoteAddress;

    }

    public static String getOriginUser(HttpServletRequest request) {
        String originUser = StringUtils.EMPTY;

        if (Objects.nonNull(request)) {
            originUser = request.getHeader("ORIGIN-USER");
        }

        return originUser;

    }

    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = StringUtils.EMPTY;

        if (Objects.nonNull(request)) {
            userAgent = request.getHeader("User-Agent");
        }

        return userAgent;

    }

    public static String getClientIdHeader(HttpServletRequest request) {
        String clientId = StringUtils.EMPTY;

        if (Objects.nonNull(request) && Objects.nonNull(request.getHeader("x-client-id"))) {
            clientId = request.getHeader("x-client-id");
        }

        return clientId;

    }

    public static String getClientSecretKeyHeader(HttpServletRequest request) {
        String clientSecretKey = StringUtils.EMPTY;

        if (Objects.nonNull(request) && Objects.nonNull(request.getHeader("x-secret-key"))) {
            clientSecretKey = request.getHeader("x-secret-key");
        }

        return clientSecretKey;

    }

    public static String getApiKey(HttpServletRequest request) {
        String clientSecretKey = StringUtils.EMPTY;

        if (Objects.nonNull(request) && Objects.nonNull(request.getHeader("x-api-key"))) {
            clientSecretKey = request.getHeader("x-api-key");
        }

        return clientSecretKey;

    }
}
