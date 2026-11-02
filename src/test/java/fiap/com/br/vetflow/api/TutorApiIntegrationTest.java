package fiap.com.br.vetflow.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TutorApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "VET")
    void fullCrudCycle() throws Exception {
        String email = "it-" + UUID.randomUUID() + "@email.com";
        String createBody = "{\"name\":\"Teste Integracao\",\"email\":\"" + email + "\",\"phone\":\"11988887777\"}";

        MvcResult created = mockMvc.perform(post("/api/tutors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        Number id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/tutors/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teste Integracao"));

        String updateBody = "{\"name\":\"Teste Atualizado\",\"email\":\"" + email + "\",\"phone\":\"11977776666\"}";
        mockMvc.perform(put("/api/tutors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teste Atualizado"))
                .andExpect(jsonPath("$.phone").value("11977776666"));

        mockMvc.perform(delete("/api/tutors/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/tutors/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "VET")
    void createRejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/tutors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Teste\",\"email\":\"invalido\",\"phone\":\"11988887777\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "VET")
    void createRejectsDuplicateEmail() throws Exception {
        String email = "dup-" + UUID.randomUUID() + "@email.com";
        String body = "{\"name\":\"Teste Duplicado\",\"email\":\"" + email + "\",\"phone\":\"11988887777\"}";

        mockMvc.perform(post("/api/tutors").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/tutors").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
