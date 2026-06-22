package BookPoint.autentificacion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import BookPoint.autentificacion.model.Autentificacion;
import BookPoint.autentificacion.repository.AutentificacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutentificacionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AutentificacionRepository autentificacionRepository;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void cleanDb() {
        autentificacionRepository.deleteAll();
    }

    @Test
    void testListarYObtenerLogin() throws Exception {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", LocalDateTime.now(), 5L);
        Autentificacion guardado = autentificacionRepository.save(auth);

        mockMvc.perform(get("/api/v1/autentificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("juan@mail.com"));

        mockMvc.perform(get("/api/v1/autentificacion/" + guardado.getIdAutentificacion()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutentificacion").value(guardado.getIdAutentificacion()));
    }

    @Test
    void testModificarYEliminar() throws Exception {
        Autentificacion auth = new Autentificacion(null, "viejo@mail.com", "1234", LocalDateTime.now(), 5L);
        Autentificacion guardado = autentificacionRepository.save(auth);

        Autentificacion datos = new Autentificacion(null, "nuevo@mail.com", "5678", null, null);

        mockMvc.perform(put("/api/v1/autentificacion/" + guardado.getIdAutentificacion())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("nuevo@mail.com"));

        mockMvc.perform(delete("/api/v1/autentificacion/" + guardado.getIdAutentificacion()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/autentificacion/" + guardado.getIdAutentificacion()))
                .andExpect(status().isNotFound());
    }
}