package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.TutorDtos.TutorRequest;
import fiap.com.br.vetflow.entity.Tutor;
import fiap.com.br.vetflow.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository tutorRepository;

    @Cacheable("tutors")
    public List<Tutor> findAll() {
        return tutorRepository.findAll();
    }

    public Page<Tutor> findAllPaged(Pageable pageable) {
        return tutorRepository.findAll(pageable);
    }

    public Tutor findById(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado: " + id));
    }

    public Tutor findByEmail(String email) {
        return tutorRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado com e-mail: " + email));
    }

    public List<Tutor> findByName(String name) {
        return tutorRepository.findByNameContainingIgnoreCase(name);
    }

    @CacheEvict(value = "tutors", allEntries = true)
    public Tutor create(TutorRequest request) {
        if (tutorRepository.existsByEmail(request.email().trim().toLowerCase()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um tutor com este e-mail.");
        return tutorRepository.save(request.toEntity());
    }

    @CacheEvict(value = "tutors", allEntries = true)
    public Tutor update(Long id, TutorRequest request) {
        Tutor tutor = findById(id);
        tutor.setName(request.name().trim());
        tutor.setPhone(request.phone().trim());
        return tutorRepository.save(tutor);
    }

    @CacheEvict(value = "tutors", allEntries = true)
    public void delete(Long id) {
        if (!tutorRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado: " + id);
        tutorRepository.deleteById(id);
    }
}
