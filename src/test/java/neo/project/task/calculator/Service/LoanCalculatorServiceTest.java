package neo.project.task.calculator.Service;


import neo.project.task.calculator.DTO.LoanOfferDto;
import neo.project.task.calculator.DTO.LoanStatementRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoanCalculatorServiceTest {

    private LoanCalculatorService service;

    @BeforeEach
    public void setUp() {
        service = new LoanCalculatorService();
    }

    private LoanStatementRequestDto createValidRequest() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setAmount(new BigDecimal("10000"));
        request.setTerm(12);
        request.setFirstName("Ivan");
        request.setLastName("Ivanov");
        request.setMiddleName("Ivanovich");
        request.setEmail("ivan@example.com");
        request.setBirthdate(LocalDate.of(1990, 1, 1));
        request.setPassportSeries("1234");
        request.setPassportNumber("123456");
        return request;
    }

    @Test
    public void testProcessLoanRequest_validData_returnsOffers() {
        LoanStatementRequestDto request = createValidRequest();

        List<LoanOfferDto> offers = service.processLoanRequest(request);

        assertThat(offers).hasSize(4);
    }

    @Test
    public void testInvalidAmount_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testInvalidEmail_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setEmail("invalid_email");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testUnderageClient_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setBirthdate(LocalDate.now().minusYears(17));

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testInvalidPassportSeries_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setPassportSeries("abcd");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testInvalidPassportNumber_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setPassportNumber("12AB56");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testTooShortFirstName_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setFirstName("I");

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }

    @Test
    public void testMissingMiddleName_throwsException() {
        LoanStatementRequestDto request = createValidRequest();
        request.setMiddleName(null);

        assertThrows(IllegalArgumentException.class, () -> service.processLoanRequest(request));
    }
}