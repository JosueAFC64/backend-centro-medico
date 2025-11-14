package cm.apiempleado.dto.response;

import cm.apiempleado.client.especialidad.EspecialidadResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MedicoPorEspecialidadResponse(
        @Schema(description = "Nombre de la especialidad", example = "Cardiología")
        EspecialidadResponse especialidad,
        @Schema(description = "Lista de datos de médicos que pertenecen a la especialidad",
        example = "{id: 1, nombreCompleto: Hans Sideral}, {id: 2, nombreCompleto: Gerald Luján}")
        List<DatosMedico> medicos
) {
    public record DatosMedico(
            @Schema(description = "Identificador único del médico", example = "1")
            Long id,
            @Schema(description = "Nombre completo del médico", example = "Gerald Luján")
            String nombreCompleto
    ){}
}
