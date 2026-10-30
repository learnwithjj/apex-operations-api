package com.apex.operations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OperationIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("apex").withUsername("apex").withPassword("test");
    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Autowired MockMvc mvc;
    @Test void createReadUpdateDeleteAndValidate() throws Exception {
        String payload = "{\"name\":\"Backup\",\"description\":\"Daily\",\"status\":\"PENDING\"}";
        String location = mvc.perform(post("/api/operations").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("Backup"))
            .andReturn().getResponse().getHeader("Location");
        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDING"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Backup\",\"status\":\"COMPLETED\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
        mvc.perform(get("/api/operations")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Backup"));
        mvc.perform(post("/api/operations").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\",\"status\":\"PENDING\"}")).andExpect(status().isBadRequest());
        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }
}
