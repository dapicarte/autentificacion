package BookPoint.autentificacion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import BookPoint.autentificacion.model.Autentificacion;
import BookPoint.autentificacion.service.AutentificacionService;

@RestController
@RequestMapping("api/v1/autentificacion")
public class AutentificacionController {
    @Autowired
    private AutentificacionService autentificacionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Autentificacion autentificacion) {
        try {
            String respuesta = autentificacionService.login(autentificacion);
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error en el servidor", HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarLogins() {
        List<Autentificacion> logins = autentificacionService.listarLogins();
        if (logins.isEmpty()) {
            return new ResponseEntity<>("No existen registros de login", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(logins, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Autentificacion busc = autentificacionService.findById(id).orElse(null);
        if (busc == null) {
            return new ResponseEntity<>("Login con id "+id+" no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(busc, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Autentificacion autentificacion){
        Autentificacion actualizado = autentificacionService.modificar(id, autentificacion);
        if (actualizado == null){
            return new ResponseEntity<>("Login con id "+id+" no existe", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if (autentificacionService.eliminar(id)){
            return new ResponseEntity<>("Login con id "+id+" eliminado",  HttpStatus.OK);
        }
        return new ResponseEntity<>("Login con id "+id+" no existe",HttpStatus.NOT_FOUND);
    }
}
