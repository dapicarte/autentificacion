package BookPoint.autentificacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BookPoint.autentificacion.repository.AutentificacionRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AutentificacionService {
    @Autowired
    private AutentificacionRepository autentificacionRepository;
}
