/*
 * Copyright (C) 2022 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.georchestra.gateway.security.ldap.extended;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.georchestra.gateway.security.GeorchestraUserMapperExtension;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link GeorchestraUserMapperExtension} that maps LDAP-authenticated token to
 * {@link GeorchestraUser} by calling {@link UsersApi#findByUsername(String)},
 * with the authentication token's principal name as argument.
 * <p>
 * Resolves only {@link GeorchestraUserNamePasswordAuthenticationToken}, using
 * its {@link GeorchestraUserNamePasswordAuthenticationToken#getConfigName()
 * configName} to disambiguate amongst different configured LDAP databases.
 * 
 * @see DemultiplexingUsersApi
 */
@RequiredArgsConstructor
class GeorchestraLdapAuthenticatedUserMapper implements GeorchestraUserMapperExtension {

    private final @NonNull DemultiplexingUsersApi users;

    @Override
    public Optional<GeorchestraUser> resolve(Authentication authToken) {
        return Optional.ofNullable(authToken)//
                .filter(GeorchestraUserNamePasswordAuthenticationToken.class::isInstance)
                .map(GeorchestraUserNamePasswordAuthenticationToken.class::cast)//
                .filter(token -> token.getPrincipal() instanceof LdapUserDetails)//
                .flatMap(this::map);
    }

    Optional<GeorchestraUser> map(GeorchestraUserNamePasswordAuthenticationToken token) {
        final LdapUserDetails principal = (LdapUserDetails) token.getPrincipal();
        final String ldapConfigName = token.getConfigName();
        final String username = principal.getUsername();

        Optional<ExtendedGeorchestraUser> user = users.findByUsername(ldapConfigName, username);
        if (user.isEmpty()) {
            user = users.findByEmail(ldapConfigName, username);
        }

        return user.map(u -> fixPrefixedRoleNames(u, token));
    }

    private GeorchestraUser fixPrefixedRoleNames(GeorchestraUser user,
            GeorchestraUserNamePasswordAuthenticationToken token) {

        final LdapUserDetailsImpl principal = (LdapUserDetailsImpl) token.getPrincipal();

        // Fix role name mismatch between authority provider (adds ROLE_ prefix) and
        // users api
        Stream<String> authorityRoleNames = token.getAuthorities().stream()
                .filter(SimpleGrantedAuthority.class::isInstance).map(GrantedAuthority::getAuthority)
                .map(this::normalize);

        Stream<String> userRoles = user.getRoles().stream().map(this::normalize);

        List<String> roles = Stream.concat(authorityRoleNames, userRoles).distinct().collect(Collectors.toList());

        user.setRoles(roles);
        if (principal.getTimeBeforeExpiration() < Integer.MAX_VALUE) {
            user.setLdapWarn(true);
            user.setLdapRemainingDays(String.valueOf(principal.getTimeBeforeExpiration() / (60 * 60 * 24)));
        } else {
            user.setLdapWarn(false);
        }

        return user;
    }

    private String normalize(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
