/*
 * Copyright (C) 2023 by the geOrchestra PSC
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
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra. If not, see <http://www.gnu.org/licenses/>.
 */
package org.georchestra.gateway.accounts.admin.ldap;

import static java.util.Objects.requireNonNull;

import org.georchestra.ds.orgs.OrgsDao;
import org.georchestra.ds.orgs.OrgsDaoImpl;
import org.georchestra.ds.roles.RoleDao;
import org.georchestra.ds.roles.RoleDaoImpl;
import org.georchestra.ds.roles.RoleProtected;
import org.georchestra.ds.users.AccountDao;
import org.georchestra.ds.users.AccountDaoImpl;
import org.georchestra.gateway.accounts.admin.AccountManager;
import org.georchestra.gateway.accounts.admin.CreateAccountUserCustomizer;
import org.georchestra.gateway.security.GeorchestraGatewaySecurityConfigProperties;
import org.georchestra.gateway.security.ldap.extended.DemultiplexingUsersApi;
import org.georchestra.gateway.security.ldap.extended.ExtendedLdapConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.pool.validation.DefaultDirContextValidator;

/**
 * Spring Boot configuration class for geOrchestra's LDAP-based account
 * management.
 * <p>
 * This class defines beans for managing LDAP user accounts, roles, and
 * organizations using Spring LDAP and pooled connections.
 * </p>
 * <p>
 * The configuration is driven by properties defined in
 * {@link GeorchestraGatewaySecurityConfigProperties}.
 * </p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GeorchestraGatewaySecurityConfigProperties.class)
public class GeorchestraLdapAccountManagementConfiguration {

    /**
     * Defines the primary {@link AccountManager} bean using LDAP as the backend.
     *
     * @param eventPublisher         the event publisher for account-related events
     * @param accountDao             the DAO for managing user accounts in LDAP
     * @param roleDao                the DAO for managing roles in LDAP
     * @param orgsDao                the DAO for managing organizations in LDAP
     * @param demultiplexingUsersApi API for resolving users based on OAuth2
     *                               credentials
     * @param configProperties       the security configuration properties
     * @return an instance of {@link LdapAccountsManager}
     */
    @Bean
    AccountManager ldapAccountsManager(//
            ApplicationEventPublisher eventPublisher, //
            AccountDao accountDao, //
            RoleDao roleDao, //
            OrgsDao orgsDao, //
            DemultiplexingUsersApi demultiplexingUsersApi,
            GeorchestraGatewaySecurityConfigProperties configProperties) {
        return new LdapAccountsManager(eventPublisher::publishEvent, accountDao, roleDao, orgsDao,
                demultiplexingUsersApi, configProperties);
    }

    /**
     * Registers a {@link CreateAccountUserCustomizer} bean to handle automatic
     * account creation when a user logs in via trusted authentication mechanisms.
     *
     * @param accountManager the account manager responsible for user retrieval and
     *                       creation
     * @return a {@link CreateAccountUserCustomizer} instance
     */
    @Bean
    CreateAccountUserCustomizer createAccountUserCustomizer(AccountManager accountManager) {
        return new CreateAccountUserCustomizer(accountManager);
    }

    /**
     * Creates an LDAP context source for connecting to a single LDAP directory.
     *
     * @param config the LDAP configuration properties
     * @return a configured {@link LdapContextSource} instance
     */
    @Bean
    LdapContextSource singleContextSource(GeorchestraGatewaySecurityConfigProperties config) {
        ExtendedLdapConfig ldapConfig = config.extendedEnabled().getFirst();
        LdapContextSource singleContextSource = new LdapContextSource();
        singleContextSource.setUrl(ldapConfig.getUrl());
        singleContextSource.setBase(ldapConfig.getBaseDn());
        singleContextSource.setUserDn(ldapConfig.getAdminDn().orElseThrow());
        singleContextSource.setPassword(ldapConfig.getAdminPassword().orElseThrow());
        return singleContextSource;
    }

    /**
     * Configures a pooling LDAP context source to optimize connection management.
     *
     * @param singleContextSource the base LDAP context source
     * @return a {@link PoolingContextSource} with connection pooling enabled
     */
    @Bean
    PoolingContextSource contextSource(LdapContextSource singleContextSource) {
        PoolingContextSource contextSource = new PoolingContextSource();
        contextSource.setContextSource(singleContextSource);
        contextSource.setDirContextValidator(new DefaultDirContextValidator());
        contextSource.setTestOnBorrow(true);
        contextSource.setMaxActive(8);
        contextSource.setMinIdle(1);
        contextSource.setMaxIdle(8);
        contextSource.setMaxTotal(-1);
        contextSource.setMaxWait(-1);
        return contextSource;
    }

    /**
     * Creates an {@link LdapTemplate} for interacting with LDAP.
     *
     * @param contextSource the pooled LDAP context source
     * @return an initialized {@link LdapTemplate}
     */
    @Bean
    LdapTemplate ldapTemplate(PoolingContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    /**
     * Creates a {@link RoleDao} implementation for managing LDAP roles.
     *
     * @param ldapTemplate the LDAP template for querying LDAP
     * @param config       the security configuration properties
     * @return a configured {@link RoleDaoImpl}
     */
    @Bean
    RoleDao roleDao(LdapTemplate ldapTemplate, GeorchestraGatewaySecurityConfigProperties config) {
        RoleDaoImpl impl = new RoleDaoImpl();
        impl.setLdapTemplate(ldapTemplate);
        impl.setRoleSearchBaseDN(config.extendedEnabled().getFirst().getRolesRdn());
        return impl;
    }

    /**
     * Creates an {@link OrgsDao} implementation for managing LDAP organizations.
     *
     * @param ldapTemplate the LDAP template for querying LDAP
     * @param config       the security configuration properties
     * @return a configured {@link OrgsDaoImpl}
     */
    @Bean
    OrgsDao orgsDao(LdapTemplate ldapTemplate, GeorchestraGatewaySecurityConfigProperties config) {
        OrgsDaoImpl impl = new OrgsDaoImpl();
        impl.setLdapTemplate(ldapTemplate);
        ExtendedLdapConfig ldapConfig = config.extendedEnabled().getFirst();
        impl.setBasePath(ldapConfig.getBaseDn());
        impl.setOrgSearchBaseDN(ldapConfig.getOrgsRdn());
        impl.setPendingOrgSearchBaseDN(ldapConfig.getPendingOrgsRdn());
        return impl;
    }

    /**
     * Creates an {@link AccountDao} implementation for managing user accounts in
     * LDAP.
     *
     * @param ldapTemplate the LDAP template for querying LDAP
     * @param config       the security configuration properties
     * @return a configured {@link AccountDaoImpl}
     */
    @Bean
    AccountDao accountDao(LdapTemplate ldapTemplate, GeorchestraGatewaySecurityConfigProperties config) {
        ExtendedLdapConfig ldapConfig = config.extendedEnabled().getFirst();
        AccountDaoImpl impl = new AccountDaoImpl(ldapTemplate);
        impl.setBasePath(ldapConfig.getBaseDn());
        impl.setUserSearchBaseDN(ldapConfig.getUsersRdn());
        impl.setRoleSearchBaseDN(ldapConfig.getRolesRdn());
        impl.setPendingUserSearchBaseDN("ou=pendingusers");
        impl.setOrgSearchBaseDN(requireNonNull(ldapConfig.getOrgsRdn()));
        impl.setPendingOrgSearchBaseDN("ou=pendingorgs");
        impl.init();
        return impl;
    }

    /**
     * Defines role protection rules for preventing modification of critical roles.
     *
     * @return a {@link RoleProtected} instance with predefined protected roles
     */
    @Bean
    RoleProtected roleProtected() {
        RoleProtected roleProtected = new RoleProtected();
        roleProtected.setListOfprotectedRoles(
                new String[] { "ADMINISTRATOR", "GN_.*", "ORGADMIN", "REFERENT", "USER", "SUPERUSER" });
        return roleProtected;
    }
}
