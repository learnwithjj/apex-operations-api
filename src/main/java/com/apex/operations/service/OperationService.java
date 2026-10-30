package com.apex.operations.service;
import com.apex.operations.dto.OperationRequest;
import com.apex.operations.model.Operation;
import com.apex.operations.repository.OperationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class OperationService {
    private final OperationRepository repository;
    public OperationService(OperationRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<Operation> list() { return repository.findAll(Sort.by("id").ascending()); }
    @Transactional(readOnly = true)
    public Operation get(Long id) { return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found")); }
    @Transactional
    public Operation create(OperationRequest request) {
        Operation operation = new Operation();
        apply(operation, request);
        return repository.save(operation);
    }
    @Transactional
    public Operation update(Long id, OperationRequest request) {
        Operation operation = get(id);
        apply(operation, request);
        return repository.save(operation);
    }
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found");
        repository.deleteById(id);
    }
    private void apply(Operation operation, OperationRequest request) {
        operation.setName(request.name());
        operation.setDescription(request.description());
        operation.setStatus(request.status());
    }
}
