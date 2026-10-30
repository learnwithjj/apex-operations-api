package com.apex.operations.dto;
import com.apex.operations.model.OperationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record OperationRequest(
    @NotBlank @Size(max=120) String name,
    @Size(max=1000) String description,
    @NotNull OperationStatus status
) {}
