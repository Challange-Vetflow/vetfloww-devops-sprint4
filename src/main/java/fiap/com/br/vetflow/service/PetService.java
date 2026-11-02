package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.PetDtos.PetRequest;
import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.entity.Species;
import fiap.com.br.vetflow.entity.Tutor;
import fiap.com.br.vetflow.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final TutorService tutorService;

    @Cacheable("pets")
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Page<Pet> findAllPaged(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado: " + id));
    }

    public List<Pet> findByTutor(Long tutorId) {
        return petRepository.findByTutorIdOrderByName(tutorId);
    }

    public List<Pet> findBySpecies(Species species) {
        return petRepository.findActiveBySpecies(species);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public Pet create(PetRequest request) {
        Tutor tutor = tutorService.findById(request.tutorId());

        if (request.birthDate().isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de nascimento não pode ser no futuro.");

        Pet pet = Pet.builder()
                .name(request.name().trim())
                .species(request.species())
                .breed(request.breed() != null ? request.breed().trim() : "Indefinida")
                .birthDate(request.birthDate())
                .weightKg(request.weightKg())
                .tutor(tutor)
                .build();

        return petRepository.save(pet);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public Pet update(Long id, PetRequest request) {
        Pet pet = findById(id);
        pet.setName(request.name().trim());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed() != null ? request.breed().trim() : "Indefinida");
        pet.setBirthDate(request.birthDate());
        pet.setWeightKg(request.weightKg());
        return petRepository.save(pet);
    }

    @CacheEvict(value = "pets", allEntries = true)
    public void delete(Long id) {
        if (!petRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado: " + id);
        petRepository.deleteById(id);
    }
}
