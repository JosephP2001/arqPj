package ec.edu.uce.library.lending.application;

import java.time.LocalDate;

/**
 * Input of {@link LendCopy}: the fields of the "Prestar ejemplar" screen (Figma D02).
 */
public record LendCopyCommand(String copyCode, String memberId, LocalDate dueDate) {
}
