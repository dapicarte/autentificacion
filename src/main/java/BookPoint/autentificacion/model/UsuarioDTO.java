package BookPoint.autentificacion.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String rol;
}
