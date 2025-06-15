package neo.project.task.calculator.Service;


import neo.project.task.calculator.DTO.LoanOfferDto;
import neo.project.task.calculator.DTO.LoanStatementRequestDto;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class LoanCalculatorService implements LoanCalculatorServiceInterface{

    @Override
    public List<LoanOfferDto> processLoanRequest(LoanStatementRequestDto request) {

        validateRequest(request);

        log.debug("Request validated successfully");
        List<LoanOfferDto> offers = new ArrayList<>();
        UUID statementId = UUID.randomUUID();

        for (boolean insurance : List.of(true, false)) {
            for (boolean salary : List.of(true, false)) {

                BigDecimal rate = calculateRate(insurance, salary);
                BigDecimal monthlyPayment = calculateMonthlyPayment(request.getAmount(), rate, request.getTerm());
                BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(request.getTerm()));

                log.debug("Combination: insurance={}, salary={}, rate={}, monthlyPayment={}, totalAmount={}",
                        insurance, salary, rate, monthlyPayment, totalAmount);

                LoanOfferDto offer = new LoanOfferDto();
                offer.setStatementId(statementId);
                offer.setRequestedAmount(request.getAmount());
                offer.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
                offer.setTerm(request.getTerm());
                offer.setMonthlyPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
                offer.setRate(rate);
                offer.setIsInsuranceEnabled(insurance);
                offer.setIsSalaryClient(salary);

                offers.add(offer);
            }
        }
        offers.sort(Comparator.comparing(LoanOfferDto::getRate));

        return offers;
    }

    private void validateRequest(LoanStatementRequestDto req) {
        log.debug("Validation start");
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        if (req.getTerm() == null || req.getTerm() <= 0)
            throw new IllegalArgumentException("Term must be positive");

        if (req.getFirstName().length() < 2 ||req.getFirstName().length() >30)
            throw new IllegalArgumentException("First name is required");

        if (req.getLastName().length()<2 || req.getLastName().length()>30)
            throw new IllegalArgumentException("Last name is required");

        if (req.getMiddleName() == null || req.getMiddleName().length() < 2 || req.getMiddleName().length() > 30)
            throw new IllegalArgumentException("Middle name is required");

        if (req.getEmail() == null || !req.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format");

        if (req.getBirthdate() == null || req.getBirthdate().isAfter(LocalDate.now().minusYears(18)))
            throw new IllegalArgumentException("User must be at least 18 years old");

        if (req.getPassportSeries() == null || !req.getPassportSeries().matches("\\d{4}"))
            throw new IllegalArgumentException("Passport series must be 4 digits");

        if (req.getPassportNumber() == null || !req.getPassportNumber().matches("\\d{6}"))
            throw new IllegalArgumentException("Passport number must be 6 digits");
        log.debug("Validation end");
    }

    private BigDecimal calculateRate(boolean insurance, boolean salary) {
        log.debug("calculateRate start");
        BigDecimal baseRate = new BigDecimal("10.0");
        if (insurance) baseRate = baseRate.subtract(new BigDecimal("1.0"));
        if (salary) baseRate = baseRate.subtract(new BigDecimal("0.5"));
        log.debug("calculateRate info: baseRate={}, salary={}, baseRate={}",
                insurance, salary, baseRate);
        return baseRate;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal rateAnnual, int termMonths) {
        log.debug("calculateMonthlyPayment start");
        BigDecimal monthlyRate = rateAnnual.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), 10, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRPowerN = monthlyRate.add(BigDecimal.ONE).pow(termMonths);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        BigDecimal answer = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        log.debug("calculateMonthlyPayment info: MonthlyPayment={}", answer);
        return answer;
    }
}

