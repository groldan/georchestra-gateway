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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

/**
 * Test suite for {@link GeorchestraLdapAuthenticatedUserMapper}
 *
 */
class GeorchestraLdapAuthenticatedUserMapperTest {

    private GeorchestraLdapAuthenticatedUserMapper mapper;
    private UsersApi mockUsers;
    private OrganizationsApi mockOrgs;

    @BeforeEach
    void before() {
        mockUsers = mock(UsersApi.class);
        mockOrgs = mock(OrganizationsApi.class);
        when(mockUsers.findByUsername(anyString())).thenReturn(Optional.empty());
        DemultiplexingUsersApi demultiplexingUsers = new DemultiplexingUsersApi(Map.of("default", mockUsers),
                Map.of("default", mockOrgs));
        mapper = new GeorchestraLdapAuthenticatedUserMapper(demultiplexingUsers);
    }

    @Test
    void testNotAUserNamePasswordAuthenticationToken() {
        Authentication auth = mock(Authentication.class);
        Optional<GeorchestraUser> resolve = mapper.resolve(auth);
        assertNotNull(resolve);
        assertTrue(resolve.isEmpty());
        verifyNoInteractions(mockUsers);
    }

    @Test
    void testNotAGeorchestraUserNamePasswordAuthenticationToken() {
        UserDetails principal = new User("testuser", "secret", List.of());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null);

        Optional<GeorchestraUser> resolve = mapper.resolve(auth);
        assertNotNull(resolve);
        assertTrue(resolve.isEmpty());

        verifyNoInteractions(mockUsers);
    }

    @Test
    void testNotAnLdapUserDetails() {
        UserDetails principal = new User("testuser", "secret", List.of());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null);

        Optional<GeorchestraUser> resolve = mapper
                .resolve(new GeorchestraUserNamePasswordAuthenticationToken("default", auth));
        assertNotNull(resolve);
        assertTrue(resolve.isEmpty());

        verifyNoInteractions(mockUsers);
    }

    @Test
    void testLdapUserDetails() {
        GeorchestraUser expected = new GeorchestraUser();
        expected.setUsername("testuser");
        LdapUserDetailsImpl principal = mock(LdapUserDetailsImpl.class);
        when(principal.getUsername()).thenReturn("ldapuser");
        when(mockUsers.findByUsername(eq("ldapuser"))).thenReturn(Optional.of(expected));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null);

        Optional<GeorchestraUser> resolve = mapper
                .resolve(new GeorchestraUserNamePasswordAuthenticationToken("default", auth));
        assertNotNull(resolve);
        GeorchestraUser actual = resolve.orElseThrow();
        assertEquals(expected, actual);

        verify(mockUsers, atLeastOnce()).findByUsername(eq("ldapuser"));
        verifyNoMoreInteractions(mockUsers);
    }
}
