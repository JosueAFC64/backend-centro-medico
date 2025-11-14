package com.CentroMedico.ApiHistoriaMedica.service;

import com.CentroMedico.ApiHistoriaMedica.client.PacienteFeignClient;
import com.CentroMedico.ApiHistoriaMedica.dto.HistoriaMedicaRequest;
import com.CentroMedico.ApiHistoriaMedica.dto.HistoriaMedicaResponse;
import com.CentroMedico.ApiHistoriaMedica.dto.PacienteAnidadoResponse;
import com.CentroMedico.ApiHistoriaMedica.dto.PacienteSimpleResponse;
import com.CentroMedico.ApiHistoriaMedica.repository.HistoriaMedica;
import com.CentroMedico.ApiHistoriaMedica.repository.HistoriaMedicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.Period;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HistoriaMedicaService {
    private final HistoriaMedicaRepository repository;
    private final PacienteFeignClient pacienteClient;


    /**
     * Registra una nueva historia médica.
     * 1. Valida la existencia del paciente via Feign.
     * 2. Calcula y valida la edad del paciente.
     * 3. Persiste la historia.
     */
    public HistoriaMedicaResponse registrar(HistoriaMedicaRequest request) {
        String dni = request.dniPaciente();
        log.info("Iniciando registro de Historia Médica para DNI: {}", dni);

        PacienteSimpleResponse pacienteData;
        try {
            pacienteData = pacienteClient.buscarPacientePorDni(dni);
        } catch (feign.FeignException.NotFound e) {
            log.error("Paciente con DNI {} no encontrado en ApiPaciente (404).", dni);
            throw new IllegalArgumentException("No se puede registrar. Paciente con DNI: " + dni + " no existe.");
        }
        if (pacienteData.idPaciente() == 0L) {
            log.error("Servicio ApiPaciente no disponible. Bloqueando registro de Historia Médica para DNI: {}.", dni);
            throw new IllegalStateException("El servicio de validación de pacientes no está disponible. Intente más tarde.");
        }
        int edadCalculada = Period.between(pacienteData.fechaNacimiento(), LocalDate.now()).getYears();
        final int EDAD_MINIMA_REGISTRO = 0;
        if (edadCalculada < EDAD_MINIMA_REGISTRO) {
            throw new IllegalArgumentException("El paciente debe tener al menos " + EDAD_MINIMA_REGISTRO +
                    " año(s) de edad para registrar una Historia Médica.");
        }
        if (repository.existsById(dni)) {
            log.warn("Ya existe una Historia Médica registrada con el DNI: {}", dni);
            throw new IllegalArgumentException("Ya existe una Historia Médica registrada para el paciente con DNI: " + dni);
        }
        HistoriaMedica historia = toEntity(request, pacienteData.idPaciente(), edadCalculada);
        HistoriaMedica historiaGuardada = repository.save(historia);
        log.info("Historia Médica registrada con ID (DNI): {}", historiaGuardada.getIdHistoriaMedica());

        return toResponse(historiaGuardada, pacienteData);
    }



    public HistoriaMedicaResponse actualizar(String dni, HistoriaMedicaRequest request) {
        log.info("Iniciando actualización de Historia Médica para DNI: {}", dni);
        PacienteSimpleResponse pacienteData;

        try {
            pacienteData = pacienteClient.buscarPacientePorDni(dni);
        } catch (feign.FeignException.NotFound e) {
            log.error("Paciente con DNI {} asociado a la historia fue eliminado.", dni);
            throw new IllegalArgumentException("El paciente asociado a esta Historia Médica ya no existe.");
        }
        if (pacienteData.idPaciente() == 0L) {
            log.error("Servicio ApiPaciente no disponible. Bloqueando actualización de Historia Médica para DNI: {}.", dni);
            throw new IllegalStateException("El servicio de validación de pacientes no está disponible. Intente más tarde.");
        }

        int edadCalculada = Period.between(pacienteData.fechaNacimiento(), LocalDate.now()).getYears();

        HistoriaMedica historiaExistente = repository.findById(dni)
                .orElseThrow(() -> new IllegalArgumentException("Historia Médica no encontrada para DNI: " + dni));

        historiaExistente.setPeso(request.peso());
        historiaExistente.setTalla(request.talla());
        historiaExistente.setEdad(edadCalculada);
        historiaExistente.setTipoSangre(request.tipoSangre());
        historiaExistente.setAlergias(request.alergias());
        historiaExistente.setAntecedentesFamiliares(request.antecedentesFamiliares());
        historiaExistente.setAntecedentesPersonales(request.antecedentesPersonales());

        HistoriaMedica historiaActualizada = repository.save(historiaExistente);

        return toResponse(historiaActualizada, pacienteData);
    }

    /**
     * Busca una historia médica por su ID (que es el DNI del paciente).
     */
    @Transactional(readOnly = true)
    public HistoriaMedicaResponse buscarPorDni(String dni) {
        log.info("Buscando Historia Médica por DNI: {}", dni);

        HistoriaMedica historia = repository.findById(dni)
                .orElseThrow(() -> new IllegalArgumentException("Historia Médica no encontrada para DNI: " + dni));
        PacienteSimpleResponse pacienteData;
        try {
            pacienteData = pacienteClient.buscarPacientePorDni(dni);
        } catch (feign.FeignException.NotFound e) {
            log.error("El paciente asociado a la historia médica (DNI: {}) fue eliminado.", dni);
            throw new IllegalArgumentException("El paciente asociado a esta Historia Médica ya no existe.");
        }
        if (pacienteData.idPaciente() == 0L) {
            log.warn("Servicio de pacientes no disponible. Devolviendo Historia Médica con datos de paciente alternativos.");
        }
        return toResponse(historia, pacienteData);
    }

    /**
     * Record privado anidado para almacenar el par (HistoriaMedica, PacienteSimpleResponse)
     * durante el proceso de búsqueda por nombre.
     * Esto evita crear un DTO innecesario fuera del servicio.
     */
    private record HistoriaPacientePair(
            HistoriaMedica historia,
            PacienteSimpleResponse paciente
    ) {}

    /**
     * Busca historias médicas buscando primero los pacientes por nombre en ApiPaciente.
     */
    @Transactional(readOnly = true)
    public List<HistoriaMedicaResponse> buscarPorNombrePaciente(String nombre) {
        log.info("Iniciando búsqueda de Historias Médicas por nombre/apellido: {}", nombre);

        List<PacienteSimpleResponse> pacientesCoincidentes = pacienteClient.buscarPacientesPorNombre(nombre);

        if (pacientesCoincidentes.isEmpty()) {
            log.warn("No se encontraron pacientes con el nombre/apellido '{}' o servicio ApiPaciente no disponible (Fallback activo).", nombre);
            return Collections.emptyList();
        }
        return pacientesCoincidentes.stream()
                .map(pacienteData -> {
                    HistoriaMedica historia = repository.findById(pacienteData.dni()).orElse(null);

                    return new HistoriaPacientePair(historia, pacienteData);
                })

                .filter(pair -> pair.historia() != null)

                .map(pair -> toResponse(pair.historia(), pair.paciente()))

                .collect(Collectors.toList());
    }

    /**
     * Verifica si existe una Historia Médica para un ID de Paciente dado.
     * Usado por ApiPaciente para validar la eliminación.
     */
    @Transactional(readOnly = true)
    public boolean existeHistoriaPorIdPaciente(Long idPaciente) {
        return repository.existsByIdPaciente(idPaciente);
    }


    private HistoriaMedica toEntity(HistoriaMedicaRequest request, Long idPaciente, Integer edadCalculada) { // <<< EDAD AÑADIDA
        return HistoriaMedica.builder()
                .idHistoriaMedica(request.dniPaciente())
                .idPaciente(idPaciente)
                .peso(request.peso())
                .talla(request.talla())
                .edad(edadCalculada) // <<< USAR LA EDAD CALCULADA
                .tipoSangre(request.tipoSangre())
                .alergias(request.alergias())
                .antecedentesFamiliares(request.antecedentesFamiliares())
                .antecedentesPersonales(request.antecedentesPersonales())
                .build();
    }

    private HistoriaMedicaResponse toResponse(HistoriaMedica historia, PacienteSimpleResponse pacienteData) {

        PacienteAnidadoResponse pacienteAnidado = PacienteAnidadoResponse.builder()
                .idPaciente(pacienteData.idPaciente())
                .nombres(pacienteData.nombres())
                .apellidos(pacienteData.apellidos())
                .fechaNacimiento(pacienteData.fechaNacimiento())
                .build();

        return HistoriaMedicaResponse.builder()
                .idHistoriaMedica(historia.getIdHistoriaMedica())
                .paciente(pacienteAnidado)
                .peso(historia.getPeso())
                .talla(historia.getTalla())
                .edad(historia.getEdad())
                .tipoSangre(historia.getTipoSangre())
                .alergias(historia.getAlergias())
                .antecedentesFamiliares(historia.getAntecedentesFamiliares())
                .antecedentesPersonales(historia.getAntecedentesPersonales())
                .fechaCreacion(historia.getFechaCreacion())
                .build();
    }
}
