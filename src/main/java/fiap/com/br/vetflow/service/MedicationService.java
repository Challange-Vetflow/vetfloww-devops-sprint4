package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.MedicationDtos.MedicationRequest;
import fiap.com.br.vetflow.entity.Medication;
import fiap.com.br.vetflow.entity.MedicationStatus;
import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PetService petService;

    public List<Medication> findAll() {
        return medicationRepository.findAll();
    }

    public Medication findById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado: " + id));
    }

    public List<Medication> findByPet(Long petId) {
        return medicationRepository.findByPetId(petId);
    }

    public List<Medication> findActive() {
        return medicationRepository.findAllActive();
    }

    public Medication create(MedicationRequest request) {
        if (request.endDate().isBefore(request.startDate()) || request.endDate().isEqual(request.startDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de fim deve ser após a data de início.");
        Pet pet = petService.findById(request.petId());
        return medicationRepository.save(Medication.builder()
                .pet(pet)
                .name(request.name().trim())
                .dosage(request.dosage().trim())
                .frequency(request.frequency().trim())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build());
    }

    public Medication update(Long id, MedicationRequest request) {
        Medication medication = findById(id);
        if (request.endDate().isBefore(request.startDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de fim deve ser após a data de início.");
        medication.setName(request.name().trim());
        medication.setDosage(request.dosage().trim());
        medication.setFrequency(request.frequency().trim());
        medication.setStartDate(request.startDate());
        medication.setEndDate(request.endDate());
        return medicationRepository.save(medication);
    }

    public Medication suspend(Long id) {
        Medication medication = findById(id);
        medication.setStatus(MedicationStatus.SUSPENDED);
        return medicationRepository.save(medication);
    }

    public Medication complete(Long id) {
        Medication medication = findById(id);
        medication.setStatus(MedicationStatus.COMPLETED);
        return medicationRepository.save(medication);
    }

    public void delete(Long id) {
        if (!medicationRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicamento não encontrado: " + id);
        medicationRepository.deleteById(id);
    }
}
