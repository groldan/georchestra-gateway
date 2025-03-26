/*
 * Copyright (C) 2021 by the geOrchestra PSC
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
package org.georchestra.gateway.security;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.georchestra.gateway.model.GatewayConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for the security settings in geOrchestra Gateway.
 * <p>
 * This configuration initializes the {@link SecurityWebFilterChain}, handling
 * authentication, authorization, and security policies.
 * </p>
 *
 * <p>
 * Instead of defining all security settings directly, this configuration relies
 * on {@link ServerHttpSecurityCustomizer} implementations to allow decoupled
 * and extensible security customization.
 * </p>
 *
 * @see ServerHttpSecurityCustomizer
 */
@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
@EnableConfigurationProperties({ GatewayConfigProperties.class })
@Slf4j(topic = "org.georchestra.gateway.security")
public class GatewaySecurityConfiguration {

    @Autowired(required = false)
    ServerLogoutSuccessHandler oidcLogoutSuccessHandler;

    private @Value("${georchestra.gateway.logoutUrl:/?logout}") String georchestraLogoutUrl;

    /**
     * Configures security settings for the gateway using available customizers.
     * <p>
     * This method:
     * <ul>
     * <li>Disables CSRF protection (expected to be handled by proxied web
     * apps).</li>
     * <li>Disables default security response headers to allow Spring Cloud Gateway
     * to manage them.</li>
     * <li>Applies a custom access denied handler.</li>
     * <li>Sets up form-based login handling.</li>
     * <li>Applies all available {@link ServerHttpSecurityCustomizer} extensions in
     * order.</li>
     * <li>Configures logout handling, using an OIDC logout handler if
     * available.</li>
     * </ul>
     * </p>
     *
     * <p>
     * The following response headers are disabled by default:
     * </p>
     * 
     * <pre>
     * <code>
     * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
     * Pragma: no-cache
     * Expires: 0
     * X-Content-Type-Options: nosniff
     * Strict-Transport-Security: max-age=31536000 ; includeSubDomains
     * X-Frame-Options: DENY
     * X-XSS-Protection: 1; mode=block
     * </code>
     * </pre>
     *
     * @param http        the {@link ServerHttpSecurity} instance
     * @param customizers the list of available {@link ServerHttpSecurityCustomizer}
     *                    implementations
     * @return the configured {@link SecurityWebFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
            List<ServerHttpSecurityCustomizer> customizers) throws Exception {

        log.info("Initializing security filter chain...");

        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.disable());
        http.exceptionHandling(handling -> handling.accessDeniedHandler(new CustomAccessDeniedHandler()));

        http.formLogin(login -> login
                .authenticationFailureHandler(new ExtendedRedirectServerAuthenticationFailureHandler("login?error"))
                .loginPage("/login"));

        sortedCustomizers(customizers).forEach(customizer -> {
            log.debug("Applying security customizer {}", customizer.getName());
            customizer.customize(http);
        });

        log.info("Security filter chain initialized");

        RedirectServerLogoutSuccessHandler defaultRedirect = new RedirectServerLogoutSuccessHandler();
        defaultRedirect.setLogoutSuccessUrl(URI.create(georchestraLogoutUrl));

        ServerHttpSecurity logoutSpec = http.formLogin(login -> login.loginPage("/login")).logout(logout -> logout
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                .logoutSuccessHandler(oidcLogoutSuccessHandler != null ? oidcLogoutSuccessHandler : defaultRedirect));

        return logoutSpec.build();
    }

    /**
     * Sorts and returns the list of custom security configurations.
     *
     * @param customizers the list of security customizers
     * @return a sorted stream of {@link ServerHttpSecurityCustomizer} instances
     */
    private Stream<ServerHttpSecurityCustomizer> sortedCustomizers(List<ServerHttpSecurityCustomizer> customizers) {
        return customizers.stream().sorted((c1, c2) -> Integer.compare(c1.getOrder(), c2.getOrder()));
    }

    /**
     * Creates a {@link GeorchestraUserMapper} to resolve user identities using the
     * configured resolvers and customizers.
     *
     * @param resolvers   the list of user resolvers
     * @param customizers the list of user customizers
     * @return an instance of {@link GeorchestraUserMapper}
     */
    @Bean
    GeorchestraUserMapper georchestraUserResolver(List<GeorchestraUserMapperExtension> resolvers,
            List<GeorchestraUserCustomizerExtension> customizers) {
        return new GeorchestraUserMapper(resolvers, customizers);
    }

    /**
     * Creates a global filter that resolves authenticated geOrchestra users in the
     * request lifecycle.
     *
     * @param resolver the {@link GeorchestraUserMapper} used to resolve users
     * @return an instance of {@link ResolveGeorchestraUserGlobalFilter}
     */
    @Bean
    ResolveGeorchestraUserGlobalFilter resolveGeorchestraUserGlobalFilter(GeorchestraUserMapper resolver) {
        return new ResolveGeorchestraUserGlobalFilter(resolver);
    }

    /**
     * Registers a custom user role mapping extension.
     * <p>
     * This extension updates user roles based on the configured mappings in
     * {@link GatewayConfigProperties#getRolesMappings()}.
     * </p>
     *
     * @param config the gateway configuration properties
     * @return an instance of {@link RolesMappingsUserCustomizer}
     */
    @Bean
    RolesMappingsUserCustomizer rolesMappingsUserCustomizer(GatewayConfigProperties config) {
        Map<String, List<String>> rolesMappings = config.getRolesMappings();
        log.info("Creating {}", RolesMappingsUserCustomizer.class.getSimpleName());
        return new RolesMappingsUserCustomizer(rolesMappings);
    }
}
