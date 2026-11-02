package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.ClinicDtos.ClinicRequest;
import fiap.com.br.vetflow.entity.Clinic;
import fiap.com.br.vetflow.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    public Clinic findById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada: " + id));
    }

    public Clinic create(ClinicRequest request) {
        if (clinicRepository.existsByNameIgnoreCase(request.name()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma clínica com este nome.");
        return clinicRepository.save(Clinic.builder()
                .name(request.name().trim())
                .address(request.address())
                .phone(request.phone())
                .email(request.email())
                .build());
    }

    public Clinic update(Long id, ClinicRequest request) {
        Clinic clinic = findById(id);
        clinic.setName(request.name().trim());
        clinic.setAddress(request.address());
        clinic.setPhone(request.phone());
        clinic.setEmail(request.email());
        return clinicRepository.save(clinic);
    }

    public void delete(Long id) {
        if (!clinicRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada: " + id);
        clinicRepository.deleteById(id);
    }
}
