package cm.apiconsultorio.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consultorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nro_consultorio", nullable = false, unique = true)
    private String nroConsultorio;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

}
