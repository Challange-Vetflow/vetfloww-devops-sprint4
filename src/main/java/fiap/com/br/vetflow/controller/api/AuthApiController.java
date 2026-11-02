package fiap.com.br.vetflow.controller.api;

import fiap.com.br.vetflow.dto.RegisterDtos.TutorRegisterRequest;
import fiap.com.br.vetflow.entity.Tutor;
import fiap.com.br.vetflow.entity.User;
import fiap.com.br.vetflow.entity.UserRole;
import fiap.com.br.vetflow.repository.TutorRepository;
import fiap.com.br.vetflow.repository.UserRepository;
import fiap.com.br.vetflow.security.VetFlowUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de autenticação em JSON, dedicados ao app mobile.
 *
 * O Spring Security padrão do projeto usa login por formulário + sessão HTTP
 * (cookie JSESSIONID), que funciona bem para as views Thymeleaf, mas devolve
 * HTML/redirect em vez de JSON — formato que um client mobile (fetch/axios)
 * não consegue consumir direito.
 *
 * Este controller reaproveita o MESMO mecanismo de sessão do Spring Security
 * (não usa JWT): ele autentica manualmente e grava o resultado na sessão HTTP,
 * exatamente como o formLogin faria. O único requisito do lado mobile é que o
 * cliente HTTP preserve e reenvie o cookie de sessão que a API devolve no
 * header Set-Cookie do /api/auth/login — em React Native/Expo isso normalmente
 * exige configurar o cliente HTTP para enviar credenciais (cookies) em toda
 * requisição subsequente.
 *
 * Nenhuma rota fora de /api/auth/** é afetada por este controller.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    // ------------------------------------------------------------------
    // POST /api/auth/login
    // ------------------------------------------------------------------
    public record LoginRequest(String email, String senha) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    request.email(), request.senha());

            Authentication authResult = authenticationManager.authenticate(authRequest);

            // Grava a autenticação na sessão HTTP — o Set-Cookie (JSESSIONID)
            // vai automaticamente na resposta. O mobile precisa guardar esse
            // cookie e reenviá-lo nas próximas chamadas.
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            User user = (User) authResult.getPrincipal();
            return ResponseEntity.ok(toUserResponse(user));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "E-mail ou senha inválidos."));
        }
    }

    // ------------------------------------------------------------------
    // POST /api/auth/register  (auto-cadastro de tutor, igual ao fluxo web)
    // ------------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody TutorRegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email) || tutorRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Já existe uma conta cadastrada com este e-mail."));
        }

        Tutor tutor = tutorRepository.save(Tutor.builder()
                .name(request.name().trim())
                .email(email)
                .phone(request.phone().trim())
                .build());

        User user = userRepository.save(User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.TUTOR)
                .tutorId(tutor.getId())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
    }

    // ------------------------------------------------------------------
    // GET /api/auth/me — usado pelo mobile para checar se a sessão ainda é válida
    // ao reabrir o app (mantém o usuário logado sem pedir senha de novo).
    // ------------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Sessão não autenticada."));
        }
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(toUserResponse(user));
    }

    // ------------------------------------------------------------------
    // POST /api/auth/logout
    // ------------------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("mensagem", "Sessão encerrada."));
    }

    private Map<String, Object> toUserResponse(User user) {
        return Map.of(
                "id", user.getId(),
                "nome", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "tutorId", user.getTutorId() == null ? "" : user.getTutorId()
        );
    }
}