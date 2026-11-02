package fiap.com.br.vetflow.controller.web;

import fiap.com.br.vetflow.dto.RegisterDtos.TutorRegisterRequest;
import fiap.com.br.vetflow.entity.Tutor;
import fiap.com.br.vetflow.entity.User;
import fiap.com.br.vetflow.entity.UserRole;
import fiap.com.br.vetflow.repository.TutorRepository;
import fiap.com.br.vetflow.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Fluxo completo 1: auto-cadastro de tutor.
 * O tutor se cadastra sozinho pelo site (fora do CRUD administrativo),
 * o sistema cria o registro de Tutor e o User de acesso vinculados na mesma operação.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new TutorRegisterRequest("", "", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") TutorRegisterRequest form,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        String email = form.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email) || tutorRepository.existsByEmail(email)) {
            model.addAttribute("erro", "Já existe uma conta cadastrada com este e-mail.");
            return "auth/register";
        }

        // Cria o registro de domínio (Tutor) e o registro de acesso (User) juntos
        Tutor tutor = tutorRepository.save(Tutor.builder()
                .name(form.name().trim())
                .email(email)
                .phone(form.phone().trim())
                .build());

        userRepository.save(User.builder()
                .name(form.name().trim())
                .email(email)
                .password(passwordEncoder.encode(form.password()))
                .role(UserRole.TUTOR)
                .tutorId(tutor.getId())
                .build());

        return "redirect:/login?registered";
    }
}
