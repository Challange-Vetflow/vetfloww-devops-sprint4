package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.MedicationDtos.MedicationRequest;
import fiap.com.br.vetflow.dto.MedicationDtos.MedicationResponse;
import fiap.com.br.vetflow.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Medications", description = "Medicamentos prescritos aos pets")
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    @Operation(summary = "Lista todos os medicamentos")
    public ResponseEntity<List<MedicationResponse>> getAll() {
        return ResponseEntity.ok(medicationService.findAll().stream()
                .map(MedicationResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca medicamento pelo Id")
    public ResponseEntity<MedicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(MedicationResponse.fromEntity(medicationService.findById(id)));
    }

    @GetMapping("/by-pet/{petId}")
    @Operation(summary = "Lista medicamentos de um pet")
    public ResponseEntity<List<MedicationResponse>> getByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(medicationService.findByPet(petId).stream()
                .map(MedicationResponse::fromEntity).toList());
    }

    @GetMapping("/active")
    @Operation(summary = "Lista todos os medicamentos ativos")
    public ResponseEntity<List<MedicationResponse>> getActive() {
        return ResponseEntity.ok(medicationService.findActive().stream()
                .map(MedicationResponse::fromEntity).toList());
    }

    @PostMapping
    @Operation(summary = "Prescreve um medicamento para um pet")
    public ResponseEntity<MedicationResponse> create(@Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MedicationResponse.fromEntity(medicationService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um medicamento")
    public ResponseEntity<MedicationResponse> update(@PathVariable Long id, @Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.ok(MedicationResponse.fromEntity(medicationService.update(id, request)));
    }

    @PutMapping("/{id}/suspend")
    @Operation(summary = "Suspende um medicamento ativo")
    public ResponseEntity<MedicationResponse> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(MedicationResponse.fromEntity(medicationService.suspend(id)));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Marca medicamento como concluído")
    public ResponseEntity<MedicationResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(MedicationResponse.fromEntity(medicationService.complete(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um medicamento pelo Id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
