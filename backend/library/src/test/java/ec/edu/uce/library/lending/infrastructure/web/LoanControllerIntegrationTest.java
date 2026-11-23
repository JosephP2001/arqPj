package ec.edu.uce.library.lending.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void lendsAnAvailableCopyAndRejectsLendingItAgainWith409() throws Exception {
        String dueDate = LocalDate.now().plusDays(15).toString();

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-00124", "1712345678", dueDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.copyCode").value("EJ-00124"))
                .andExpect(jsonPath("$.memberId").value("1712345678"))
                .andExpect(jsonPath("$.dueDate").value(dueDate));

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-00124", "1798765432", dueDate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"))
                .andExpect(jsonPath("$.dueDate").value(dueDate));
    }

    @Test
    void lendingTheSampleLentCopyReturns409() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-00456", "1712345678", LocalDate.now().plusDays(15).toString())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"))
                .andExpect(jsonPath("$.dueDate").value(sampleLentCopyDueDate().toString()));
    }

    @Test
    void lendingAnUnknownCopyReturns404() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-99999", "1712345678", LocalDate.now().plusDays(15).toString())))
                .andExpect(status().isNotFound());
    }

    @Test
    void br02_returnsALentCopyAndCatalogRecordsTheReturn() throws Exception {
        String dueDate = LocalDate.now().plusDays(15).toString();
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-00123", "1712345678", dueDate)))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/loans/EJ-00123/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.copyCode").value("EJ-00123"))
                .andExpect(jsonPath("$.memberId").value("1712345678"))
                .andExpect(jsonPath("$.returnedOn").value(LocalDate.now().toString()));

        mvc.perform(get("/api/copy-usages/EJ-00123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timesReturned").value(1));

        mvc.perform(post("/api/loans/EJ-00123/return"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not lent"));

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("EJ-00123", "1798765432", dueDate)))
                .andExpect(status().isCreated());
    }

    @Test
    void returningAnUnknownCopyReturns404() throws Exception {
        mvc.perform(post("/api/loans/EJ-99999/return"))
                .andExpect(status().isNotFound());
    }

    /** Due date of the sample copy EJ-00456 (see SampleCopiesLoader): 10/12/2026, or today + 15 once it has passed. */
    private static LocalDate sampleLentCopyDueDate() {
        LocalDate figmaDueDate = LocalDate.of(2026, 12, 10);
        LocalDate today = LocalDate.now();
        return figmaDueDate.isBefore(today) ? today.plusDays(15) : figmaDueDate;
    }

    private static String body(String copyCode, String memberId, String dueDate) {
        return """
                {"copyCode": "%s", "memberId": "%s", "dueDate": "%s"}
                """.formatted(copyCode, memberId, dueDate);
    }
}
