package BookPoint.autentificacion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import BookPoint.autentificacion.model.Autentificacion;
import BookPoint.autentificacion.service.AutentificacionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutentificacionController.class)
@ActiveProfiles("test")
public class AutentificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutentificacionService autentificacionService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testLoginExitoso() throws Exception {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        Mockito.when(autentificacionService.login(any(Autentificacion.class)))
                .thenReturn("Bienvenido Juan Perez, su rol es: ADMIN");

        mockMvc.perform(post("/api/v1/autentificacion/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(content().string("Bienvenido Juan Perez, su rol es: ADMIN"));
    }

    @Test
    void testLoginError() throws Exception {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        Mockito.when(autentificacionService.login(any(Autentificacion.class)))
                .thenThrow(new RuntimeException("Servicio no disponible"));

        mockMvc.perform(post("/api/v1/autentificacion/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isConflict());
    }

    @Test
    void testListarLogins() throws Exception {
        Autentificacion a1 = new Autentificacion(1L, "juan@mail.com", "1234", LocalDateTime.now(), 5L);
        Autentificacion a2 = new Autentificacion(2L, "ana@mail.com", "abcd", LocalDateTime.now(), 6L);

        Mockito.when(autentificacionService.listarLogins()).thenReturn(Arrays.asList(a1, a2));

        mockMvc.perform(get("/api/v1/autentificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].correo", is("juan@mail.com")))
                .andExpect(jsonPath("$[1].correo", is("ana@mail.com")));
    }

    @Test
    void testListarLoginsVacio() throws Exception {
        Mockito.when(autentificacionService.listarLogins()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/autentificacion"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByIdExistente() throws Exception {
        Autentificacion auth = new Autentificacion(1L, "juan@mail.com", "1234", LocalDateTime.now(), 5L);

        Mockito.when(autentificacionService.findById(1L)).thenReturn(Optional.of(auth));

        mockMvc.perform(get("/api/v1/autentificacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutentificacion").value(1L))
                .andExpect(jsonPath("$.correo").value("juan@mail.com"));
    }

    @Test
    void testFindByIdNoExistente() throws Exception {
        Mockito.when(autentificacionService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/autentificacion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateExistente() throws Exception {
        Autentificacion datos = new Autentificacion(null, "nuevo@mail.com", "5678", null, null);
        Autentificacion actualizado = new Autentificacion(1L, "nuevo@mail.com", "5678", LocalDateTime.now(), 5L);

        Mockito.when(autentificacionService.modificar(eq(1L), any(Autentificacion.class)))
                .thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/autentificacion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutentificacion").value(1L))
                .andExpect(jsonPath("$.correo").value("nuevo@mail.com"));
    }

    @Test
    void testUpdateNoExistente() throws Exception {
        Autentificacion datos = new Autentificacion(null, "nuevo@mail.com", "5678", null, null);

        Mockito.when(autentificacionService.modificar(eq(99L), any(Autentificacion.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/v1/autentificacion/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteExistente() throws Exception {
        Mockito.when(autentificacionService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/autentificacion/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteNoExistente() throws Exception {
        Mockito.when(autentificacionService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/autentificacion/99"))
                .andExpect(status().isNotFound());
    }
}