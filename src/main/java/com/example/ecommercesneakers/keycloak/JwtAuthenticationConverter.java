package com.example.ecommercesneakers.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static String CLIENT_NAME = "eCommerceSneakersClient";

    @SuppressWarnings("unchecked")
    public AbstractAuthenticationToken convert(final Jwt source) {
        String subject = source.getSubject();
        Map<String, Object> resourceAccess = source.getClaim("resource_access");
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(CLIENT_NAME);
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
        Set<GrantedAuthority> authorities = resourceRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }
}
