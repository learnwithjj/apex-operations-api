package com.apex.operations.repository;
import com.apex.operations.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OperationRepository extends JpaRepository<Operation, Long> {}
