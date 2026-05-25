package BookPoint.autentificacion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import BookPoint.autentificacion.model.Autentificacion;
import BookPoint.autentificacion.model.UsuarioDTO;
import BookPoint.autentificacion.repository.AutentificacionRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AutentificacionService {
    @Autowired
    private AutentificacionRepository autentificacionRepository;
    @Autowired
    private RestTemplate restTemplate;

    public String login(Autentificacion autentificacion) {
        try {
            String urlUsuario = "http://localhost:8083/api/usuarios/correo/" + autentificacion.getCorreo();
            UsuarioDTO usuario = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);

            if (usuario == null) {
                return "Credenciales incorrectas";
            }

            if (!usuario.getPassword().equals(autentificacion.getPassword())) {
                return "Credenciales incorrectas";
            }
            autentificacion.setIdUsuario(usuario.getId());
            autentificacion.setFechaLogin(LocalDate.now());
            autentificacionRepository.save(autentificacion);

            System.out.println("*************************");
            System.out.println(usuario);
            System.out.println("*************************");

            return "Bienvenido " + usuario.getNombre() + " " + usuario.getApellido()+ ", su rol es: " + usuario.getRol();

        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Usuario no disponible: " + e.getMessage());
            System.out.println("*************************");
            return "Servicio de autentificacion no disponible, intente mas tarde";
        }
    }

    public List<Autentificacion> listarLogins() {
        return autentificacionRepository.findAll();
    }
}

