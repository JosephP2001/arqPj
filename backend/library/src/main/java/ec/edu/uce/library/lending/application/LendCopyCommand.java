package ec.edu.uce.library.lending.application;

import java.time.LocalDate;

/**
 * Input of {@link LendCopy}.
 */
public record LendCopyCommand(String copyCode, String memberId, LocalDate dueDate) {
}
