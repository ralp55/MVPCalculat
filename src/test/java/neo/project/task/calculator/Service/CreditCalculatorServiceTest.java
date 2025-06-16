package neo.project.task.calculator.Service;


import neo.project.task.calculator.DTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreditCalculatorServiceTest {

    private CreditCalculationService service;

    private ScoringDataDto createBaseScoringData() {
        ScoringDataDto scoring = new ScoringDataDto();
        scoring.setAmount(BigDecimal.valueOf(240000));
        scoring.setTerm(24);
        scoring.setFirstName("Ivan");
        scoring.setLastName("Petrov");
        scoring.setMiddleName("Ivanovich");
        scoring.setBirthdate(LocalDate.of(1990, 1, 1));
        scoring.setPassportSeries("1234");
        scoring.setPassportNumber("123456");
        scoring.setGender(Gender.MALE);
        scoring.setMaritalStatus(MaritalStatus.MARRIED);
        scoring.setDependentAmount(2);

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setSalary(BigDecimal.valueOf(30000));
        employment.setWorkExperienceCurrent(6);
        employment.setWorkExperienceTotal(36);

        scoring.setEmployment(employment);
        return scoring;
    }

    @BeforeEach
    void setUp() {
        service = new CreditCalculationService();
    }

    @Test
    void testAmountIsNull() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setAmount(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testAmountIsNotNull() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setAmount(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testAmountIsNegative() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setAmount(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testTermNullOrNonPositive() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setTerm(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setTerm(0);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testInvalidFirstName() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setFirstName("A");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setFirstName("VeryLongNameOverThirtyCharacters123");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setFirstName("Iv@n");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testInvalidBirthdate() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setBirthdate(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setBirthdate(LocalDate.now().minusYears(17));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setBirthdate(LocalDate.now().minusYears(70));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testInvalidPassportSeries() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setPassportSeries("12A4");

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testInvalidEmploymentStatus1() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setEmployment(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }
    @Test
    void testInvalidEmploymentStatus() {
        ScoringDataDto scoring = createBaseScoringData();
        EmploymentDto emp = scoring.getEmployment();
        emp.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testSalaryTooLowOrUnemployedThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setEmployment(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        EmploymentDto emp = new EmploymentDto();
        emp.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        emp.setSalary(BigDecimal.valueOf(5000));
        emp.setWorkExperienceCurrent(1);
        emp.setWorkExperienceTotal(5);
        scoring.setEmployment(emp);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        emp.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        emp.setSalary(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        emp.setSalary(BigDecimal.valueOf(30000));
        assertDoesNotThrow(() -> service.calculateCredit(scoring));
    }

    @Test
    void testNullLastNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setLastName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
        assertTrue(ex.getMessage().contains("Last name"));
    }

    @Test
    void testShortLastNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setLastName("A");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
        assertTrue(ex.getMessage().contains("Last name"));
    }

    @Test
    void testNullMiddleNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setMiddleName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
        assertTrue(ex.getMessage().contains("Middle name"));
    }

    @Test
    void testShortMiddleNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.setMiddleName("I");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
        assertTrue(ex.getMessage().contains("Middle name"));
    }

    @Test
    void testSalaryTooLow() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.getEmployment().setSalary(BigDecimal.valueOf(9000));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testValidInputReturnsCreditDto() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.getEmployment().setSalary(BigDecimal.valueOf(20000));

        CreditDto result = service.calculateCredit(scoring);
        assertNotNull(result);
        assertTrue(result.getPsk().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.valueOf(240000), result.getAmount());
    }

    @Test
    void testScoringAdjustmentsBusinessOwner() {
        ScoringDataDto scoring = createBaseScoringData();
        EmploymentDto emp = scoring.getEmployment();
        emp.setEmploymentStatus(EmploymentStatus.BUSINESS_OWNER);
        emp.setWorkExperienceCurrent(1);
        emp.setWorkExperienceTotal(12);
        emp.setSalary(BigDecimal.valueOf(20000));

        CreditDto result = service.calculateCredit(scoring);
        assertNotNull(result);
        assertTrue(result.getRate().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testAgeBoundaryValid() {
        ScoringDataDto scoring = createBaseScoringData();
        EmploymentDto emp = scoring.getEmployment();
        emp.setSalary(BigDecimal.valueOf(20000));

        scoring.setBirthdate(LocalDate.now().minusYears(20));
        assertDoesNotThrow(() -> service.calculateCredit(scoring));

        scoring.setBirthdate(LocalDate.now().minusYears(65));
        assertDoesNotThrow(() -> service.calculateCredit(scoring));
    }

    @Test
    void testSuccessfulCreditCalculation() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setAmount(BigDecimal.valueOf(500_000));
        scoring.setTerm(12);
        scoring.setIsInsuranceEnabled(true);
        scoring.setIsSalaryClient(true);
        scoring.setFirstName("Ivan");
        scoring.setLastName("Ivanov");
        scoring.setMiddleName("Ivanovich");
        scoring.setBirthdate(LocalDate.of(1990, 1, 1));
        scoring.setPassportSeries("1234");
        scoring.setPassportNumber("123456");
        scoring.setGender(Gender.MALE);
        scoring.setMaritalStatus(MaritalStatus.MARRIED);
        scoring.setDependentAmount(0);

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setSalary(BigDecimal.valueOf(150_000));
        employment.setWorkExperienceCurrent(24);
        employment.setWorkExperienceTotal(60);
        scoring.setEmployment(employment);

        CreditDto result = service.calculateCredit(scoring);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500_000), result.getAmount());
        assertEquals(12, result.getTerm());
        assertTrue(result.getIsInsuranceEnabled());
        assertTrue(result.getIsSalaryClient());

        assertEquals(new BigDecimal("2.50"), result.getRate());

        assertNotNull(result.getMonthlyPayment());
        assertNotNull(result.getPsk());
        assertEquals(12, result.getPaymentSchedule().size());
    }




    @Test
    void testFemaleDivorcedGivesRateBonus() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setGender(Gender.FEMALE);
        scoring.setMaritalStatus(MaritalStatus.NON_MARRIED);
        scoring.setFirstName("Ivana");
        scoring.setLastName("Petrova");
        scoring.setMiddleName("Ivanovna");
        scoring.setBirthdate(LocalDate.of(1995, 1, 1));
        scoring.setAmount(BigDecimal.valueOf(100_000));
        scoring.setTerm(12);
        scoring.setPassportSeries("1234");
        scoring.setPassportNumber("123456");

        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setSalary(BigDecimal.valueOf(20_000));
        employment.setWorkExperienceCurrent(5);
        employment.setWorkExperienceTotal(36);
        scoring.setEmployment(employment);

        CreditDto result = service.calculateCredit(scoring);

        assertNotNull(result);
        assertTrue(result.getRate().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testNullSalaryThrows() {
        ScoringDataDto scoring = createBaseScoringData();
        scoring.getEmployment().setSalary(null);

        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testInvalidPassportNumberThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setPassportNumber(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setPassportNumber("12345");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setPassportNumber("1234567");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setPassportNumber("abc123");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }

    @Test
    void testInvalidLastNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setLastName(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setLastName("A");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setLastName("ThisNameIsWayTooLongForTheValidationCheck");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setLastName("Petro#v");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testInvalidMiddleNameThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setMiddleName(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setMiddleName("I");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setMiddleName("ThisMiddleNameIsWayTooLongToBeValid");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));

        scoring.setMiddleName("Ivan#vich");
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }



    @Test
    void testAgeLessThan20Throws() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setBirthdate(LocalDate.now().minusYears(19));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testAgeMoreThan65Throws() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setBirthdate(LocalDate.now().minusYears(66));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testEmploymentNullThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.setEmployment(null);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }


    @Test
    void testEmploymentStatusUnemployedThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }



    @Test
    void testSalaryTooLowThrows() {
        ScoringDataDto scoring = createBaseScoringData();

        scoring.getEmployment().setSalary(BigDecimal.valueOf(0));
        assertThrows(IllegalArgumentException.class, () -> service.calculateCredit(scoring));
    }
}
