package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.VaccineDtos.VaccineRequest;
import fiap.com.br.vetflow.dto.VaccineDtos.VaccineResponse;
import fiap.com.br.vetflow.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccines")
@RequiredArgsConstructor
@Tag(name = "Vaccines", description = "Registro de vacinas dos pets")
public class VaccineController {

    private final VaccineService vaccineService;

    @GetMapping
    @Operation(summary = "Lista todas as vacinas registradas")
    public ResponseEntity<List<VaccineResponse>> getAll() {
        return ResponseEntity.ok(vaccineService.findAll().stream()
                .map(VaccineResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca vacina pelo Id")
    public ResponseEntity<VaccineResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(VaccineResponse.fromEntity(vaccineService.findById(id)));
    }

    @GetMapping("/by-pet/{petId}")
    @Operation(summary = "Lista vacinas de um pet")
    public ResponseEntity<List<VaccineResponse>> getByPet(@PathVariable Long petId) {
        return ResponseEntity.ok(vaccineService.findByPet(petId).stream()
                .map(VaccineResponse::fromEntity).toList());
    }

    @GetMapping("/expired")
    @Operation(summary = "Lista vacinas vencidas (próxima dose no passado)")
    public ResponseEntity<List<VaccineResponse>> getExpired() {
        return ResponseEntity.ok(vaccineService.findExpired().stream()
                .map(VaccineResponse::fromEntity).toList());
    }

    @GetMapping("/due-soon")
    @Operation(summary = "Lista vacinas com dose nos próximos N dias")
    public ResponseEntity<List<VaccineResponse>> getDueSoon(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(vaccineService.findDueSoon(days).stream()
                .map(VaccineResponse::fromEntity).toList());
    }

    @PostMapping
    @Operation(summary = "Registra uma vacina aplicada em um pet")
    public ResponseEntity<VaccineResponse> create(@Valid @RequestBody VaccineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VaccineResponse.fromEntity(vaccineService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de uma vacina registrada")
    public ResponseEntity<VaccineResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody VaccineRequest request) {
        return ResponseEntity.ok(VaccineResponse.fromEntity(vaccineService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um registro de vacina")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
