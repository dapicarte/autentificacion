package BookPoint.autentificacion.service;

import BookPoint.autentificacion.model.Autentificacion;
import BookPoint.autentificacion.model.UsuarioDTO;
import BookPoint.autentificacion.repository.AutentificacionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutentificacionServiceTest {

    @Mock
    private AutentificacionRepository autentificacionRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AutentificacionService autentificacionService;

    private UsuarioDTO crearUsuario(String password) {
        UsuarioDTO u = new UsuarioDTO();
        u.setIdUsuario(5L);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setEmailCliente("juan@mail.com");
        u.setPassword(password);
        u.setRol("ADMIN");
        return u;
    }

    // ----------------------------
    // login
    @Test
    void testLoginExitoso() {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(crearUsuario("1234"));
        when(autentificacionRepository.save(any(Autentificacion.class))).thenReturn(auth);

        String resultado = autentificacionService.login(auth);

        assertEquals("Bienvenido Juan Perez, su rol es: ADMIN", resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(UsuarioDTO.class));
        verify(autentificacionRepository, times(1)).save(any(Autentificacion.class));
    }

    @Test
    void testLoginUsuarioNull() {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(null);

        String resultado = autentificacionService.login(auth);

        assertEquals("Credenciales incorrectas", resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(UsuarioDTO.class));
        verify(autentificacionRepository, never()).save(any(Autentificacion.class));
    }

    @Test
    void testLoginPasswordIncorrecta() {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenReturn(crearUsuario("otraClave"));

        String resultado = autentificacionService.login(auth);

        assertEquals("Credenciales incorrectas", resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(UsuarioDTO.class));
        verify(autentificacionRepository, never()).save(any(Autentificacion.class));
    }

    @Test
    void testLoginUsuarioNotFound() {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null);

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class))).thenThrow(notFound);

        String resultado = autentificacionService.login(auth);

        assertEquals("Credenciales incorrectas", resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(UsuarioDTO.class));
        verify(autentificacionRepository, never()).save(any(Autentificacion.class));
    }

    @Test
    void testLoginServicioNoDisponible() {
        Autentificacion auth = new Autentificacion(null, "juan@mail.com", "1234", null, null);

        when(restTemplate.getForObject(anyString(), eq(UsuarioDTO.class)))
                .thenThrow(new RuntimeException("Conexión rechazada"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> autentificacionService.login(auth));

        assertEquals("Servicio de autentificacion no disponible, intente mas tarde", exception.getMessage());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(UsuarioDTO.class));
        verify(autentificacionRepository, never()).save(any(Autentificacion.class));
    }

    // ----------------------------
    // listarLogin
    @Test
    void testListarLogins() {
        Autentificacion a1 = new Autentificacion(1L, "juan@mail.com", "1234", LocalDateTime.now(), 5L);
        Autentificacion a2 = new Autentificacion(2L, "ana@mail.com", "abcd", LocalDateTime.now(), 6L);

        when(autentificacionRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        List<Autentificacion> resultado = autentificacionService.listarLogins();

        assertEquals(2, resultado.size());
        assertEquals("juan@mail.com", resultado.get(0).getCorreo());
        assertEquals("ana@mail.com", resultado.get(1).getCorreo());

        verify(autentificacionRepository, times(1)).findAll();
    }

    // ----------------------------
    // findById
    @Test
    void testFindByIdExistente() {
        Autentificacion auth = new Autentificacion(1L, "juan@mail.com", "1234", LocalDateTime.now(), 5L);

        when(autentificacionRepository.findById(1L)).thenReturn(Optional.of(auth));

        Optional<Autentificacion> resultado = autentificacionService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdAutentificacion());
        assertEquals("juan@mail.com", resultado.get().getCorreo());

        verify(autentificacionRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(autentificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Autentificacion> resultado = autentificacionService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(autentificacionRepository, times(1)).findById(99L);
    }

    // ----------------------------
    // para el modificar
    @Test
    void testModificarExistente() {
        Autentificacion existente = new Autentificacion(1L, "viejo@mail.com", "1234", LocalDateTime.now(), 5L);
        Autentificacion datos = new Autentificacion(null, "nuevo@mail.com", "5678", null, null);
        Autentificacion actualizado = new Autentificacion(1L, "nuevo@mail.com", "5678", LocalDateTime.now(), 5L);

        when(autentificacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(autentificacionRepository.save(existente)).thenReturn(actualizado);

        Autentificacion resultado = autentificacionService.modificar(1L, datos);

        assertNotNull(resultado);
        assertEquals("nuevo@mail.com", existente.getCorreo());
        assertEquals("5678", existente.getPassword());

        verify(autentificacionRepository, times(1)).findById(1L);
        verify(autentificacionRepository, times(1)).save(existente);
    }

    @Test
    void testModificarNoExistente() {
        Autentificacion datos = new Autentificacion(null, "nuevo@mail.com", "5678", null, null);

        when(autentificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Autentificacion resultado = autentificacionService.modificar(99L, datos);

        assertNull(resultado);

        verify(autentificacionRepository, times(1)).findById(99L);
        verify(autentificacionRepository, never()).save(any(Autentificacion.class));
    }

    // ---------------------------------------------------------------
    // para eliminar
    // ---------------------------------------------------------------
    @Test
    void testEliminarExistente() {
        when(autentificacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(autentificacionRepository).deleteById(1L);

        boolean resultado = autentificacionService.eliminar(1L);

        assertTrue(resultado);

        verify(autentificacionRepository, times(1)).existsById(1L);
        verify(autentificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarNoExistente() {
        when(autentificacionRepository.existsById(99L)).thenReturn(false);

        boolean resultado = autentificacionService.eliminar(99L);

        assertFalse(resultado);

        verify(autentificacionRepository, times(1)).existsById(99L);
        verify(autentificacionRepository, never()).deleteById(anyLong());
    }
}