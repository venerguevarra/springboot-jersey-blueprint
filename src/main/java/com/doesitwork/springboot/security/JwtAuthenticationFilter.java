package com.doesitwork.springboot.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.flogger.FluentLogger;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static final String AUTHORIZATION_BEARER = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        final String header = request.getHeader(AUTHORIZATION_HEADER);
        String username = null;
        String authorizationToken = null;

        if (header != null && header.startsWith(AUTHORIZATION_BEARER)) {
            authorizationToken = header.replace(AUTHORIZATION_BEARER, StringUtils.EMPTY);

            try {
                username = tokenProvider.getUsernameFromToken(authorizationToken);
            } catch (IllegalArgumentException e) {
                logger.atSevere().log("an_error_occured_during_getting_username_from_token");

            } catch (ExpiredJwtException e) {
                logger.atSevere().log("the_token_is_expired_and_not_valid_anymore");

            } catch (SignatureException e) {
                logger.atSevere().log("authentication_failed_username_or_password_invalid");

            }
        }

        if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (tokenProvider.validateToken(authorizationToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthentication(authorizationToken,
                                                                                                     SecurityContextHolder.getContext().getAuthentication(),
                                                                                                     userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.atSevere().log("authenticateUser=%s", username);

            }
        }

        chain.doFilter(request, res);
    }
}
