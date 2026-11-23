package ec.edu.uce.library.lending.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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
                        .content(body("UCE-0002", "1712345678", dueDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.copyCode").value("UCE-0002"))
                .andExpect(jsonPath("$.memberId").value("1712345678"))
                .andExpect(jsonPath("$.dueDate").value(dueDate));

        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("UCE-0002", "1798765432", dueDate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"));
    }

    @Test
    void lendingTheSampleLentCopyReturns409() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("UCE-0004", "1712345678", LocalDate.now().plusDays(15).toString())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("The copy is not available"));
    }

    @Test
    void lendingAnUnknownCopyReturns404() throws Exception {
        mvc.perform(post("/api/loans").contentType(MediaType.APPLICATION_JSON)
                        .content(body("UCE-9999", "1712345678", LocalDate.now().plusDays(15).toString())))
                .andExpect(status().isNotFound());
    }

    private static String body(String copyCode, String memberId, String dueDate) {
        return """
                {"copyCode": "%s", "memberId": "%s", "dueDate": "%s"}
                """.formatted(copyCode, memberId, dueDate);
    }
}
