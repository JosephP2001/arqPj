package ec.edu.uce.library.lending.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

record LendCopyRequest(@NotBlank String copyCode, @NotBlank String memberId, @NotNull LocalDate dueDate) {
}
