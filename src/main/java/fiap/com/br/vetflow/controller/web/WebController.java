package fiap.com.br.vetflow.controller.web;

import fiap.com.br.vetflow.dto.AppointmentDtos.AppointmentRequest;
import fiap.com.br.vetflow.dto.VaccineDtos.VaccineRequest;
import fiap.com.br.vetflow.entity.*;
import fiap.com.br.vetflow.repository.UserRepository;
import fiap.com.br.vetflow.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebController {

    private final PetService petService;
    private final TutorService tutorService;
    private final ClinicService clinicService;
    private final AppointmentService appointmentService;
    private final VaccineService vaccineService;
    private final UserRepository userRepository;

    // ------------------------------------------------------------------
    // Dashboard — visão muda conforme o papel do usuário logado
    // ------------------------------------------------------------------
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);

        if (user.getRole() == UserRole.TUTOR) {
            List<Pet> pets = petService.findByTutor(user.getTutorId());
            model.addAttribute("pets", pets);
        } else {
            model.addAttribute("pendingAppointments", appointmentService.findPending());
            model.addAttribute("expiredVaccines", vaccineService.findExpired());
        }

        return "dashboard";
    }

    // ------------------------------------------------------------------
    // Listagem de pets (tutor vê só os seus; vet vê todos)
    // ------------------------------------------------------------------
    @GetMapping("/pets")
    public String listPets(@AuthenticationPrincipal User user, Model model) {
        List<Pet> pets = user.getRole() == UserRole.TUTOR
                ? petService.findByTutor(user.getTutorId())
                : petService.findAll();
        model.addAttribute("pets", pets);
        model.addAttribute("user", user);
        return "pets/list";
    }

    @GetMapping("/pets/{id}")
    public String petDetail(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        Pet pet = petService.findById(id);
        model.addAttribute("pet", pet);
        model.addAttribute("appointments", appointmentService.findByPet(id));
        model.addAttribute("vaccines", vaccineService.findByPet(id));
        model.addAttribute("user", user);
        return "pets/detail";
    }

    // ------------------------------------------------------------------
    // FLUXO COMPLETO 1: Tutor agenda uma consulta para o pet
    // Passo 1: escolhe o pet -> Passo 2: escolhe clínica/data/tipo -> confirma
    // ------------------------------------------------------------------
    @GetMapping("/appointments/schedule/{petId}")
    public String scheduleForm(@PathVariable Long petId, @AuthenticationPrincipal User user, Model model) {
        Pet pet = petService.findById(petId);

        // Tutor só pode agendar para os próprios pets
        if (user.getRole() == UserRole.TUTOR && !pet.getTutor().getId().equals(user.getTutorId())) {
            return "redirect:/web/pets?erro=acesso-negado";
        }

        model.addAttribute("pet", pet);
        model.addAttribute("clinics", clinicService.findAll());
        model.addAttribute("types", AppointmentType.values());
        model.addAttribute("user", user);
        return "appointments/schedule";
    }

    @PostMapping("/appointments/schedule/{petId}")
    public String scheduleSubmit(@PathVariable Long petId,
                                  @RequestParam Long clinicId,
                                  @RequestParam String scheduledAt,
                                  @RequestParam AppointmentType type,
                                  @RequestParam(required = false) String notes,
                                  @AuthenticationPrincipal User user,
                                  Model model) {
        Pet pet = petService.findById(petId);

        if (user.getRole() == UserRole.TUTOR && !pet.getTutor().getId().equals(user.getTutorId())) {
            return "redirect:/web/pets?erro=acesso-negado";
        }

        try {
            AppointmentRequest request = new AppointmentRequest(
                    petId, clinicId, LocalDateTime.parse(scheduledAt), type, notes);
            Appointment created = appointmentService.create(request);
            return "redirect:/web/pets/" + petId + "?agendado=" + created.getId();
        } catch (Exception e) {
            model.addAttribute("pet", pet);
            model.addAttribute("clinics", clinicService.findAll());
            model.addAttribute("types", AppointmentType.values());
            model.addAttribute("user", user);
            model.addAttribute("erro", e.getMessage());
            return "appointments/schedule";
        }
    }

    // ------------------------------------------------------------------
    // FLUXO COMPLETO 2: Veterinário registra uma vacina aplicada no pet
    // Passo 1: busca o pet -> Passo 2: preenche dados da vacina -> confirma
    // Acesso restrito a VET (protegido também no SecurityConfig).
    // ------------------------------------------------------------------
    @GetMapping("/vaccines/apply/{petId}")
    public String applyVaccineForm(@PathVariable Long petId, @AuthenticationPrincipal User user, Model model) {
        Pet pet = petService.findById(petId);
        model.addAttribute("pet", pet);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("user", user);
        return "vaccines/apply";
    }

    @PostMapping("/vaccines/apply/{petId}")
    public String applyVaccineSubmit(@PathVariable Long petId,
                                      @RequestParam String vaccineName,
                                      @RequestParam String appliedAt,
                                      @RequestParam String nextDoseAt,
                                      @RequestParam(required = false) String batch,
                                      @AuthenticationPrincipal User user,
                                      Model model) {
        Pet pet = petService.findById(petId);
        try {
            VaccineRequest request = new VaccineRequest(
                    petId, vaccineName, LocalDate.parse(appliedAt), LocalDate.parse(nextDoseAt), batch);
            Vaccine created = vaccineService.create(request);
            return "redirect:/web/pets/" + petId + "?vacinado=" + created.getId();
        } catch (Exception e) {
            model.addAttribute("pet", pet);
            model.addAttribute("today", LocalDate.now());
            model.addAttribute("user", user);
            model.addAttribute("erro", e.getMessage());
            return "vaccines/apply";
        }
    }
}
