package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.VaccineDtos.VaccineRequest;
import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.entity.Vaccine;
import fiap.com.br.vetflow.repository.VaccineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final PetService petService;

    public List<Vaccine> findAll() {
        return vaccineRepository.findAll();
    }

    public Vaccine findById(Long id) {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacina não encontrada: " + id));
    }

    public List<Vaccine> findByPet(Long petId) {
        return vaccineRepository.findByPetIdOrderByAppliedAtDesc(petId);
    }

    public List<Vaccine> findExpired() {
        return vaccineRepository.findExpired(LocalDate.now());
    }

    public List<Vaccine> findDueSoon(int days) {
        return vaccineRepository.findDueBetween(LocalDate.now(), LocalDate.now().plusDays(days));
    }

    public Vaccine create(VaccineRequest request) {
        validateDates(request);
        Pet pet = petService.findById(request.petId());
        return vaccineRepository.save(Vaccine.builder()
                .pet(pet)
                .vaccineName(request.vaccineName().trim())
                .appliedAt(request.appliedAt())
                .nextDoseAt(request.nextDoseAt())
                .batch(request.batch())
                .build());
    }

    public Vaccine update(Long id, VaccineRequest request) {
        Vaccine vaccine = findById(id);
        validateDates(request);
        vaccine.setVaccineName(request.vaccineName().trim());
        vaccine.setAppliedAt(request.appliedAt());
        vaccine.setNextDoseAt(request.nextDoseAt());
        vaccine.setBatch(request.batch());
        return vaccineRepository.save(vaccine);
    }

    public void delete(Long id) {
        if (!vaccineRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacina não encontrada: " + id);
        vaccineRepository.deleteById(id);
    }

    private void validateDates(VaccineRequest request) {
        if (request.nextDoseAt().isBefore(request.appliedAt()) || request.nextDoseAt().isEqual(request.appliedAt()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Próxima dose deve ser após a data de aplicação.");
    }
}
