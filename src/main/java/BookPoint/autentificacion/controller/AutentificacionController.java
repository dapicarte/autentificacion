package BookPoint.autentificacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import BookPoint.autentificacion.service.AutentificacionService;

@RestController
@RequestMapping("api/autentificacion")
public class AutentificacionController {
    @Autowired
    private AutentificacionService autentificacionService;
}
