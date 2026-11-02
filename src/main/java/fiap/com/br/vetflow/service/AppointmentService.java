package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.AppointmentDtos.AppointmentRequest;
import fiap.com.br.vetflow.entity.Appointment;
import fiap.com.br.vetflow.entity.Clinic;
import fiap.com.br.vetflow.entity.Pet;
import fiap.com.br.vetflow.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetService petService;
    private final ClinicService clinicService;

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado: " + id));
    }

    public List<Appointment> findByPet(Long petId) {
        return appointmentRepository.findByPetId(petId);
    }

    public List<Appointment> findByClinic(Long clinicId) {
        return appointmentRepository.findByClinicId(clinicId);
    }

    public List<Appointment> findPending() {
        return appointmentRepository.findPending(LocalDateTime.now());
    }

    public Appointment create(AppointmentRequest request) {
        Pet pet = petService.findById(request.petId());
        Clinic clinic = clinicService.findById(request.clinicId());
        return appointmentRepository.save(Appointment.builder()
                .pet(pet)
                .clinic(clinic)
                .scheduledAt(request.scheduledAt())
                .type(request.type())
                .notes(request.notes())
                .build());
    }

    public Appointment update(Long id, AppointmentRequest request) {
        Appointment appointment = findById(id);
        Pet pet = petService.findById(request.petId());
        Clinic clinic = clinicService.findById(request.clinicId());
        appointment.setPet(pet);
        appointment.setClinic(clinic);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setType(request.type());
        appointment.setNotes(request.notes());
        return appointmentRepository.save(appointment);
    }

    public Appointment complete(Long id) {
        Appointment appointment = findById(id);
        appointment.setCompleted(true);
        return appointmentRepository.save(appointment);
    }

    public void delete(Long id) {
        if (!appointmentRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado: " + id);
        appointmentRepository.deleteById(id);
    }
}
