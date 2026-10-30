package com.apex.operations.controller;
import com.apex.operations.dto.OperationRequest;
import com.apex.operations.model.Operation;
import com.apex.operations.service.OperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/api/operations")
public class OperationController {
    private final OperationService service;
    public OperationController(OperationService service) { this.service = service; }
    @GetMapping public List<Operation> list() { return service.list(); }
    @GetMapping("/{id}") public Operation get(@PathVariable Long id) { return service.get(id); }
    @PostMapping public ResponseEntity<Operation> create(@Valid @RequestBody OperationRequest request) {
        Operation created = service.create(request);
        return ResponseEntity.created(URI.create("/api/operations/" + created.getId())).body(created);
    }
    @PutMapping("/{id}") public Operation update(@PathVariable Long id, @Valid @RequestBody OperationRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
