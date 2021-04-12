package com.doesitwork.springboot.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.doesitwork.springboot.domain.enums.UserType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider implements Serializable {
    private static final long serialVersionUID = 4156559735141082598L;


    public static final String AUTHORIZATION_BEARER = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_ROLES = "roles";
    public static final String AUTHORIZATION_USER_ID = "userId";
    public static final String AUTHORIZATION_USER_TYPE = "userType";
    public static final String ORIGIN_IP_ADDRESS = "originIpAddress";
    public static final String USER_AGENT = "userAgent";
    public static final String SIGNING_KEY = "4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(";
    public static final long AUTHORIZATION_TOKEN_VALIDITITY_SECONDS = 60 * 120;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String authorizationToken) {
        return getClaimFromToken(authorizationToken, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUserRoles(final String authorizationToken) {
        final Claims claims = getAllClaimsFromToken(authorizationToken);
        return claims.get(AUTHORIZATION_ROLES, String.class);

    }

    public String getOriginIp(final String authorizationToken) {
        final Claims claims = getAllClaimsFromToken(authorizationToken);
        return claims.get(ORIGIN_IP_ADDRESS, String.class);

    }

    public String getSubscriberId(final String authorizationToken) {
        final Claims claims = getAllClaimsFromToken(authorizationToken);
        return claims.get(AUTHORIZATION_USER_ID, String.class);

    }

    public String getUserAgent(final String authorizationToken) {
        final Claims claims = getAllClaimsFromToken(authorizationToken);
        return claims.get(USER_AGENT, String.class);

    }

    private Claims getAllClaimsFromToken(String authorizationToken) {
        return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(authorizationToken).getBody();
    }

    public Boolean isTokenExpired(String authorizationToken) {
        final Date expiration = getExpirationDateFromToken(authorizationToken);
        return expiration.before(new Date());
    }

    public String generateToken(Authentication authentication, UUID userId, String originIp, String userAgent, UserType userType) {
        final String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(AUTHORIZATION_ROLES, authorities)
                   .claim(ORIGIN_IP_ADDRESS, originIp)
                   .claim(USER_AGENT, userAgent)
                   .claim(AUTHORIZATION_USER_ID, userId)
                   .claim(AUTHORIZATION_USER_TYPE, userType.value())
                   .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_VALIDITITY_SECONDS * 1000))
                   .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();
        final Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORIZATION_ROLES).toString().split(","))
                                                                         .map(SimpleGrantedAuthority::new)
                                                                         .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, StringUtils.EMPTY, authorities);
    }

}
