package BookPoint.autentificacion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "autentificacion_users")
public class Autentificacion {
    private Long idAutentificacion;
    private String correo;
    private String password;
}
