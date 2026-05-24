package BookPoint.autentificacion.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String correo;
    private String password;
}
