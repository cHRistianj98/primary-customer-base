package com.github.christianj98.primarycustomerbase.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class SpringFoxConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCheckSwaggerDocumentation() throws Exception {
        // when + then
        mockMvc.perform(get("/v2/api-docs"))
                .andExpect(status().isOk());

    }

}
