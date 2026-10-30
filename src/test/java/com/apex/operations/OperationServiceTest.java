package com.apex.operations;
import com.apex.operations.dto.OperationRequest;
import com.apex.operations.model.Operation;
import com.apex.operations.model.OperationStatus;
import com.apex.operations.repository.OperationRepository;
import com.apex.operations.service.OperationService;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class OperationServiceTest {
    @Test void updateChangesFields() {
        OperationRepository repo = mock(OperationRepository.class);
        Operation existing = new Operation();
        existing.setName("Old");
        existing.setStatus(OperationStatus.PENDING);
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        Operation result = new OperationService(repo).update(1L,
            new OperationRequest("New", "Updated", OperationStatus.COMPLETED));
        assertEquals("New", result.getName());
        assertEquals(OperationStatus.COMPLETED, result.getStatus());
        verify(repo).save(existing);
    }
    @Test void missingOperationReturns404() {
        OperationRepository repo = mock(OperationRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
            () -> new OperationService(repo).get(99L));
        assertEquals(404, ex.getStatusCode().value());
    }
}
