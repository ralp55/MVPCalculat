package neo.project.task.calculator.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import neo.project.task.calculator.CalculatorApplication;
import neo.project.task.calculator.DTO.CreditDto;
import neo.project.task.calculator.DTO.ScoringDataDto;
import neo.project.task.calculator.Service.CreditCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditController.class)
@ContextConfiguration(classes = CalculatorApplication.class)
class CreditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditCalculationService calculationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreditControllerConstructor() {
        CreditController controller = new CreditController(calculationService);
        assertNotNull(controller);
    }

    @Test
    void testCalculateEndpointReturnsOk() throws Exception {
        ScoringDataDto request = new ScoringDataDto();
        request.setAmount(java.math.BigDecimal.valueOf(100000));
        request.setTerm(12);

        CreditDto mockResponse = new CreditDto();
        mockResponse.setAmount(request.getAmount());
        mockResponse.setTerm(request.getTerm());

        when(calculationService.calculateCredit(any(ScoringDataDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(calculationService, times(1)).calculateCredit(any(ScoringDataDto.class));
    }
}
