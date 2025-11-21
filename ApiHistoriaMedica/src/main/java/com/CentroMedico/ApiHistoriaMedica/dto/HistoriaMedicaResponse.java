package com.CentroMedico.ApiHistoriaMedica.dto;

import com.CentroMedico.ApiHistoriaMedica.client.atencionmedica.AtencionMedicaFeignResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record HistoriaMedicaResponse(

        String idHistoriaMedica,
        PacienteAnidadoResponse paciente,

        Double peso,
        Double talla,
        Integer edad,
        String tipoSangre,
        String alergias,
        String antecedentesFamiliares,
        String antecedentesPersonales,
        LocalDate fechaCreacion,
        List<AtencionMedicaFeignResponse> atenciones
) {
}
