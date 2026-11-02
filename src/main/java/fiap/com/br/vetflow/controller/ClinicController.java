package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.ClinicDtos.ClinicRequest;
import fiap.com.br.vetflow.dto.ClinicDtos.ClinicResponse;
import fiap.com.br.vetflow.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinics", description = "Clínicas veterinárias parceiras")
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping
    @Operation(summary = "Lista todas as clínicas")
    public ResponseEntity<List<ClinicResponse>> getAll() {
        return ResponseEntity.ok(clinicService.findAll().stream()
                .map(ClinicResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca clínica pelo Id")
    public ResponseEntity<ClinicResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ClinicResponse.fromEntity(clinicService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova clínica parceira")
    public ResponseEntity<ClinicResponse> create(@Valid @RequestBody ClinicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClinicResponse.fromEntity(clinicService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de uma clínica")
    public ResponseEntity<ClinicResponse> update(@PathVariable Long id, @Valid @RequestBody ClinicRequest request) {
        return ResponseEntity.ok(ClinicResponse.fromEntity(clinicService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma clínica pelo Id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clinicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
