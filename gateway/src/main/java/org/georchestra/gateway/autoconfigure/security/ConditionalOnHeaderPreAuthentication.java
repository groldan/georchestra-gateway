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
package org.georchestra.gateway.autoconfigure.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.georchestra.gateway.security.preauth.HeaderPreauthConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Condition that enables a bean if header-based pre-authentication is enabled.
 * <p>
 * This annotation ensures that a component is only loaded when the
 * {@code georchestra.gateway.security.preauth.enabled} property is set to
 * {@code true}.
 * </p>
 *
 * @see HeaderPreauthConfigProperties
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = HeaderPreauthConfigProperties.ENABLED_PROPERTY, havingValue = "true", matchIfMissing = false)
public @interface ConditionalOnHeaderPreAuthentication {

}
