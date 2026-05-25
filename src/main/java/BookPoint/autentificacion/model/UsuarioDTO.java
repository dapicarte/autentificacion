package BookPoint.autentificacion.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String emailCliente;
    private String password;
    private String rol;
}
