/**
 * Copyright (C) 2016-2019 Expedia, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.road.paver;

import static java.util.Collections.singletonList;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

@Configuration
public class PaverSwaggerConfiguration {
  private static final String BASIC_AUTH = "basicAuth";

  @Bean
  @Lazy
  SecurityConfiguration paverSecurity() {
    return SecurityConfigurationBuilder
        .builder()
        .realm("Data Highway")
        .appName("paver")
        .scopeSeparator(",")
        .additionalQueryStringParams(null)
        .useBasicAuthenticationWithAccessCodeGrant(false)
        .build();
  }

  @Bean
  public Docket paverSwagger() {
    return new Docket(SWAGGER_2)
        .groupName("paver")
        .securitySchemes(Collections.singletonList(new BasicAuth(BASIC_AUTH)))
        .securityContexts(Collections.singletonList(securityContext()))
        .apiInfo(new ApiInfoBuilder().title("Paver").build())
        .useDefaultResponseMessages(false)
        .tags(new Tag("road", "Administer roads"), new Tag("schema", "Administer schemas"))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.regex("/paver.*"))
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext
        .builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex("/paver.*"))
        .build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("all", "all");
    return singletonList(new SecurityReference(BASIC_AUTH, new AuthorizationScope[] { authorizationScope }));
  }
}
