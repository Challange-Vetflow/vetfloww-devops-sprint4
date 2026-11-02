package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.AppointmentDtos.AppointmentRequest;
import fiap.com.br.vetflow.dto.AppointmentDtos.AppointmentResponse;
import fiap.com.br.vetflow.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Agendamentos e consultas dos pets")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Lista todos os agendamentos")
    public ResponseEntity<List<AppointmentResponse>> getAll() {
        return ResponseEntity.ok(appointmentService.findAll().stream()
                .map(AppointmentResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca agendamento pelo Id")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AppointmentResponse.fromEntity(appointmentService.findById(id)));
    }

    @GetMapping("/by-pet/{petId}")
    @Operation(summary = "Lista agendamentos de um pet")
    public ResponseEntity<List<AppointmentResponse>> getByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(appointmentService.findByPet(petId).stream()
                .map(AppointmentResponse::fromEntity).toList());
    }

    @GetMapping("/by-clinic/{clinicId}")
    @Operation(summary = "Lista agendamentos de uma clínica")
    public ResponseEntity<List<AppointmentResponse>> getByClinic(@PathVariable Long clinicId) {
        return ResponseEntity.ok(appointmentService.findByClinic(clinicId).stream()
                .map(AppointmentResponse::fromEntity).toList());
    }

    @GetMapping("/pending")
    @Operation(summary = "Lista agendamentos pendentes (futuros e não concluídos)")
    public ResponseEntity<List<AppointmentResponse>> getPending() {
        return ResponseEntity.ok(appointmentService.findPending().stream()
                .map(AppointmentResponse::fromEntity).toList());
    }

    @PostMapping
    @Operation(summary = "Cria um novo agendamento")
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppointmentResponse.fromEntity(appointmentService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um agendamento")
    public ResponseEntity<AppointmentResponse> update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(AppointmentResponse.fromEntity(appointmentService.update(id, request)));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Marca um agendamento como concluído")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(AppointmentResponse.fromEntity(appointmentService.complete(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um agendamento pelo Id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
