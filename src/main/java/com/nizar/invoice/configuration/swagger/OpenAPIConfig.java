package com.nizar.invoice.configuration.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPIConf() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");

        Info info = new Info()
                .title("invoice app");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }


}
