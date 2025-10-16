package com.pharmacovigilance.mcpagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/pharmacovigilance}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pharmacovigilance MCP Agent API")
                        .description("""
                            A comprehensive Spring Boot application that integrates Model Context Protocol (MCP) 
                            and AI agents for automated pharmacovigilance workflow management. This system provides 
                            intelligent analysis of adverse drug events using ChatGPT integration and automated 
                            processing workflows.
                            
                            ## Features
                            - **MCP Server Integration**: RESTful API endpoints for pharmacovigilance data access
                            - **AI-Powered Analysis**: ChatGPT integration for causality assessment and risk analysis
                            - **Automated Workflows**: Intelligent agent-based processing of adverse events
                            - **Comprehensive Data Models**: Patient, Drug, Adverse Event, and Follow-up Action entities
                            - **Real-time Processing**: Asynchronous processing with scheduled tasks
                            - **Pattern Detection**: AI-driven analysis of adverse event patterns and trends
                            
                            ## Authentication
                            Currently, the API does not require authentication for development purposes. 
                            In production, implement proper security measures.
                            
                            ## Rate Limiting
                            AI analysis endpoints may have rate limits based on your OpenAI API plan.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pharmacovigilance Team")
                                .email("support@pharmacovigilance.com")
                                .url("https://github.com/pharmacovigilance/mcp-agent"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.pharmacovigilance.com")
                                .description("Production Server")
                ));
    }
}


