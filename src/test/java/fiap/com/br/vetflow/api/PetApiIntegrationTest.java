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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PetApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "VET")
    void fullCrudCycle() throws Exception {
        String createBody = "{\"name\":\"Rex Teste\",\"species\":\"DOG\",\"breed\":\"Labrador\","
                + "\"birthDate\":\"2022-03-15\",\"weightKg\":12.5,\"tutorId\":1}";

        MvcResult created = mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rex Teste"))
                .andExpect(jsonPath("$.tutorId").value(1))
                .andReturn();

        Number id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/pets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breed").value("Labrador"));

        String updateBody = "{\"name\":\"Rex Teste\",\"species\":\"DOG\",\"breed\":\"Golden Retriever\","
                + "\"birthDate\":\"2022-03-15\",\"weightKg\":13.0,\"tutorId\":1}";
        mockMvc.perform(put("/api/pets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.breed").value("Golden Retriever"));

        mockMvc.perform(delete("/api/pets/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/pets/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "VET")
    void createRejectsFutureBirthDate() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Futuro\",\"species\":\"DOG\",\"breed\":\"Labrador\","
                                + "\"birthDate\":\"2999-01-01\",\"weightKg\":10.0,\"tutorId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
