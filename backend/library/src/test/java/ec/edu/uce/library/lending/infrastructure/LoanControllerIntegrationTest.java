package ec.edu.uce.library.lending.infrastructure;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CopyRepository copies;

    private static String lendBody(String copyCode) {
        LocalDate dueDate = LocalDate.now().plusDays(15);
        return """
                {"copyCode": "%s", "memberId": "1712345678", "dueDate": "%s"}
                """.formatted(copyCode, dueDate);
    }

    @Test
    void lendsAnAvailableCopyAndRejectsLendingItAgainWith409() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(lendBody("EJ-00123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.copyCode").value("EJ-00123"))
                .andExpect(jsonPath("$.memberId").value("1712345678"));

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(lendBody("EJ-00123")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"));
    }

    @Test
    void lendingTheSampleLentCopyReturns409() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(lendBody("EJ-00456")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"))
                .andExpect(jsonPath("$.dueDate").value("2026-12-10"));
    }

    @Test
    void lendingAnUnknownCopyReturns404() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON).content(lendBody("EJ-99999")))
                .andExpect(status().isNotFound());
    }

    @Test
    void br02_returnsALentCopyAndLeavesItAvailable() throws Exception {
        mvc.perform(post("/api/loans/EJ-02021/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.copyCode").value("EJ-02021"))
                .andExpect(jsonPath("$.memberId").value("1712345678"));

        assertThat(copies.findById(new CopyId("EJ-02021")))
                .hasValueSatisfying(copy -> assertThat(copy.isAvailable()).isTrue());

        mvc.perform(post("/api/loans/EJ-02021/return"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not lent"));
    }

    @Test
    void returningAnUnknownCopyReturns404() throws Exception {
        mvc.perform(post("/api/loans/EJ-99999/return"))
                .andExpect(status().isNotFound());
    }
}
