package fiap.com.br.vetflow.controller;

import fiap.com.br.vetflow.dto.PetDtos.PetRequest;
import fiap.com.br.vetflow.dto.PetDtos.PetResponse;
import fiap.com.br.vetflow.entity.Species;
import fiap.com.br.vetflow.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Gerenciamento dos animais de estimação")
public class PetController {

    private final PetService petService;

    @GetMapping
    @Operation(summary = "Lista todos os pets com paginação e ordenação")
    public ResponseEntity<Page<PetResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort) {
        Page<PetResponse> result = petService.findAllPaged(PageRequest.of(page, size, Sort.by(sort)))
                .map(PetResponse::fromEntity);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca pet pelo Id")
    public ResponseEntity<PetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(PetResponse.fromEntity(petService.findById(id)));
    }

    @GetMapping("/by-tutor/{tutorId}")
    @Operation(summary = "Lista pets de um tutor específico")
    public ResponseEntity<List<PetResponse>> getByTutor(@PathVariable Long tutorId) {
        List<PetResponse> result = petService.findByTutor(tutorId).stream()
                .map(PetResponse::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-species")
    @Operation(summary = "Lista pets por espécie")
    public ResponseEntity<List<PetResponse>> getBySpecies(@RequestParam Species species) {
        List<PetResponse> result = petService.findBySpecies(species).stream()
                .map(PetResponse::fromEntity).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo pet")
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PetResponse.fromEntity(petService.create(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados do pet")
    public ResponseEntity<PetResponse> update(@PathVariable Long id, @Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(PetResponse.fromEntity(petService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um pet pelo Id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
