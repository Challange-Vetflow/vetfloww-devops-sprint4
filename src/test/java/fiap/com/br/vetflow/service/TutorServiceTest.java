package fiap.com.br.vetflow.service;

import fiap.com.br.vetflow.dto.TutorDtos.TutorRequest;
import fiap.com.br.vetflow.entity.Tutor;
import fiap.com.br.vetflow.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private TutorService tutorService;

    @Test
    void createNormalizesFieldsAndSaves() {
        TutorRequest request = new TutorRequest("  Carlos Silva  ", "  CARLOS@Email.com ", " 11911111111 ");
        when(tutorRepository.existsByEmail("carlos@email.com")).thenReturn(false);
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tutor saved = tutorService.create(request);

        assertEquals("Carlos Silva", saved.getName());
        assertEquals("carlos@email.com", saved.getEmail());
        assertEquals("11911111111", saved.getPhone());
        verify(tutorRepository).save(any(Tutor.class));
    }

    @Test
    void createRejectsDuplicateEmail() {
        when(tutorRepository.existsByEmail("ana@email.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> tutorService.create(new TutorRequest("Ana Paula", "ana@email.com", "11999999999")));

        assertEquals(400, ex.getStatusCode().value());
        verify(tutorRepository, never()).save(any(Tutor.class));
    }

    @Test
    void findByIdThrowsNotFoundWhenMissing() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> tutorService.findById(99L));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateChangesNameAndPhoneKeepingEmail() {
        Tutor existing = Tutor.builder().id(1L).name("Old Name").email("old@email.com").phone("11111111").build();
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tutor updated = tutorService.update(1L, new TutorRequest(" New Name ", "old@email.com", " 22222222 "));

        assertEquals("New Name", updated.getName());
        assertEquals("22222222", updated.getPhone());
        assertEquals("old@email.com", updated.getEmail());
    }

    @Test
    void deleteRemovesExistingTutor() {
        when(tutorRepository.existsById(1L)).thenReturn(true);

        tutorService.delete(1L);

        verify(tutorRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsNotFoundWhenMissing() {
        when(tutorRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> tutorService.delete(99L));

        assertEquals(404, ex.getStatusCode().value());
        verify(tutorRepository, never()).deleteById(any());
    }
}
