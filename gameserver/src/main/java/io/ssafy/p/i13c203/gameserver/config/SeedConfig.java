package io.ssafy.p.i13c203.gameserver.config;

import io.ssafy.p.i13c203.gameserver.config.property.AppSeedProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppSeedProperties.class)
public class SeedConfig {}
