package com.CentroMedico.ApiPaciente.service;

import com.CentroMedico.ApiPaciente.client.HistoriaMedicaFeignClient;
import com.CentroMedico.ApiPaciente.dto.*;
import com.CentroMedico.ApiPaciente.repository.Paciente;
import com.CentroMedico.ApiPaciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PacienteService {
    private final PacienteRepository repository;
    private final HistoriaMedicaFeignClient historiaMedicaClient;

    /**
     * Registra un nuevo paciente, validando que el DNI no exista previamente.
     * @param request Datos del paciente a registrar.
     * @return PacienteResponse con los datos del paciente registrado.
     * @throws IllegalArgumentException Si ya existe un paciente con el mismo DNI.
     */
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Proceso de registro de paciente con DNI: {} iniciado", request.dni());

        if (repository.existsByDni(request.dni())) {
            log.warn("El DNI {} ya se encuentra registrado. Lanzando excepción.", request.dni());
            throw new IllegalArgumentException("Ya existe un paciente registrado con el DNI: " + request.dni());
        }

        Paciente paciente = toEntity(request);
        paciente.validar();


        Paciente pacienteGuardado = repository.save(paciente);
        log.info("Paciente con ID: {} registrado correctamente.", pacienteGuardado.getIdPaciente());


        return toResponse(pacienteGuardado);
    }

    /**
     * Lista todos los pacientes registrados.
     * @return Lista de PacienteResponse.
     */
    @Transactional(readOnly = true)
    public List<PacienteSumResponse> listar() {
        log.info("Inicio del proceso de listar pacientes.");
        List<Paciente> pacientes = repository.findAll();
        log.info("Se encontraron {} pacientes.", pacientes.size());

        return pacientes.stream()
                .map(this::toSumResponse)
                .toList();
    }

    /**
     * Actualiza los datos de un paciente existente.
     * @param id ID del paciente a actualizar.
     * @param request Datos actualizados del paciente.
     * @return PacienteResponse con los datos actualizados.
     * @throws IllegalArgumentException Si el paciente no se encuentra o si el DNI actualizado ya existe.
     */
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        log.info("Proceso de actualización de paciente con ID: {} iniciado.", id);

        Paciente pacienteExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));


        if (!pacienteExistente.getDni().equals(request.dni())) {
            if (repository.existsByDni(request.dni())) {
                throw new IllegalArgumentException("El DNI proporcionado ya pertenece a otro paciente: " + request.dni());
            }
        }

        Paciente pacienteActualizado = mapUpdateRequestToEntity(pacienteExistente, request);
        pacienteActualizado.validar();

        Paciente pacienteGuardado = repository.save(pacienteActualizado);
        log.info("Paciente con ID: {} actualizado correctamente.", pacienteGuardado.getIdPaciente());

        return toResponse(pacienteGuardado);
    }

    /**
     * Elimina fisicamente un paciente, solo si no tiene una historia medica registrada .
     * @param id ID del paciente a eliminar.
     * @throws IllegalArgumentException Si el paciente no se encuentra (404).
     */
    public void eliminar(Long id) {
        log.warn("Proceso de eliminación de paciente con ID: {} iniciado.", id);

        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        boolean tieneHistoria = historiaMedicaClient.existeHistoriaPorIdPaciente(paciente.getIdPaciente());

        if (tieneHistoria) {
            log.warn("El paciente con ID: {} no puede ser eliminado porque tiene una Historia Médica asociada.", id);
            throw new IllegalStateException("El paciente no puede ser eliminado porque tiene una Historia Médica asociada.");
        }

        repository.delete(paciente);
        log.warn("Paciente con ID: {} eliminado satisfactoriamente.", id);
    }

    /**
     * Busca un paciente por su ID.
     * @param id ID del paciente.
     * @return PacienteResponse con los datos.
     * @throws IllegalArgumentException Si el paciente no se encuentra (404).
     */
    @Transactional(readOnly = true)
    public PacienteResponse buscarPorId(Long id) {
        log.info("Buscando paciente por ID: {}", id);
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con ID: " + id));

        return toResponse(paciente);
    }

    /**
     * Busca pacientes por una cadena en nombres o apellidos.
     * Devuelve la lista de PacienteResponse (DTO COMPLETO para el frontend).
     * @param nombre Cadena de búsqueda.
     * @return Lista de PacienteResponse.
     */
    @Transactional(readOnly = true)
    public List<PacienteResponse> buscarPorNombre(String nombre) {
        log.info("Buscando pacientes por nombre/apellido que contengan: {}", nombre);

        List<Paciente> pacientes = repository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, nombre);

        log.info("Se encontraron {} pacientes coincidentes.", pacientes.size());

        return pacientes.stream()
                .map(this::toResponse) // <<<< CAMBIADO: Ahora mapea al DTO completo
                .collect(Collectors.toList());
    }

    /**
     * Busca un paciente por su DNI y devuelve la respuesta COMPLETA (para el frontend).
     * @param dni DNI del paciente.
     * @return PacienteResponse con TODOS los datos.
     * @throws IllegalArgumentException Si el paciente no se encuentra (404).
     */
    @Transactional(readOnly = true)
    public PacienteResponse buscarPorDni(String dni) {
        log.info("Buscando paciente por DNI: {}", dni);
        Paciente paciente = repository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con DNI: " + dni));

        return toResponse(paciente);
    }

    /**
     * Metodo para uso exclusivo de microservicios.
     * Busca un paciente por su DNI y devuelve la respuesta simplificada.
     * @param dni DNI del paciente.
     * @return PacienteSimpleResponse con datos esenciales.
     */
    @Transactional(readOnly = true)
    public PacienteSimpleResponse buscarPorDniSimple(String dni) {
        log.info("Buscando paciente por DNI para microservicio: {}", dni);
        Paciente paciente = repository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con DNI: " + dni));

        return toSimpleResponse(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteClientResponse brindarDatosSimples(String dni) {
        log.info("Buscando paciente por DNI para microservicio: {}", dni);
        Paciente paciente = repository.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con DNI: " + dni));

        return toClientResponse(paciente);
    }

    private Paciente toEntity(PacienteRequest request) {
        return Paciente.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .dni(request.dni())
                .telefono(request.telefono())
                .correo(request.correo())
                .fechaNacimiento(request.fechaNacimiento())
                .telefonoEmergencia(request.telefonoEmergencia())
                .contactoEmergencia(request.contactoEmergencia())
                .direccion(request.direccion())
                .build();
    }

    private PacienteResponse toResponse(Paciente paciente) {
        return PacienteResponse.builder()
                .idPaciente(paciente.getIdPaciente())
                .nombres(paciente.getNombres())
                .apellidos(paciente.getApellidos())
                .dni(paciente.getDni())
                .telefono(paciente.getTelefono())
                .correo(paciente.getCorreo())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .telefonoEmergencia(paciente.getTelefonoEmergencia())
                .contactoEmergencia(paciente.getContactoEmergencia())
                .direccion(paciente.getDireccion())
                .build();
    }

    private PacienteSumResponse toSumResponse(Paciente p) {
        return new PacienteSumResponse(
                p.getIdPaciente(),
                p.getNombres(),
                p.getApellidos(),
                p.getDni()
        );
    }

    private Paciente mapUpdateRequestToEntity(Paciente paciente, PacienteRequest request) {
        paciente.setNombres(request.nombres());
        paciente.setApellidos(request.apellidos());
        paciente.setDni(request.dni());
        paciente.setTelefono(request.telefono());
        paciente.setCorreo(request.correo());
        paciente.setFechaNacimiento(request.fechaNacimiento());
        paciente.setTelefonoEmergencia(request.telefonoEmergencia());
        paciente.setContactoEmergencia(request.contactoEmergencia());
        paciente.setDireccion(request.direccion());
        return paciente;
    }

    private PacienteSimpleResponse toSimpleResponse(Paciente paciente) {
        return PacienteSimpleResponse.builder()
                .idPaciente(paciente.getIdPaciente())
                .nombres(paciente.getNombres())
                .apellidos(paciente.getApellidos())
                .dni(paciente.getDni())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .build();
    }

    private PacienteClientResponse toClientResponse(Paciente p) {
        return new PacienteClientResponse(
                p.getNombres() + " " + p.getApellidos(),
                p.getDni(),
                p.getDireccion()
        );
    }

}
