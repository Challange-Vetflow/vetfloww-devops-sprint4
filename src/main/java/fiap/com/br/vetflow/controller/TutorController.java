package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.TutorDtos.TutorRequest;
import fiap.com.br.vetflow.dto.TutorDtos.TutorResponse;
import fiap.com.br.vetflow.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
@Tag(name = "Tutors", description = "Responsáveis pelos pets")
public class TutorController {

    private final TutorService tutorService;

    @GetMapping
    @Operation(summary = "Lista todos os tutores com paginação")
    public ResponseEntity<Page<TutorResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort) {
        Page<TutorResponse> result = tutorService.findAllPaged(PageRequest.of(page, size, Sort.by(sort)))
                .map(TutorResponse::fromEntity);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca tutor pelo Id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tutor encontrado"),
                    @ApiResponse(responseCode = "404", description = "Não encontrado")
            })
    public ResponseEntity<TutorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(TutorResponse.fromEntity(tutorService.findById(id)));
    }

    @GetMapping("/by-email")
    @Operation(summary = "Busca tutor pelo e-mail")
    public ResponseEntity<TutorResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(TutorResponse.fromEntity(tutorService.findByEmail(email)));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca tutores pelo nome (contém)")
    public ResponseEntity<List<TutorResponse>> searchByName(@RequestParam String name) {
        List<TutorResponse> result = tutorService.findByName(name).stream()
                .map(TutorResponse::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo tutor")
    public ResponseEntity<TutorResponse> create(@Valid @RequestBody TutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TutorResponse.fromEntity(tutorService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados do tutor")
    public ResponseEntity<TutorResponse> update(@PathVariable Long id, @Valid @RequestBody TutorRequest request) {
        return ResponseEntity.ok(TutorResponse.fromEntity(tutorService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um tutor pelo Id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tutorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
