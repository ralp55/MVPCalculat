package neo.project.task.calculator.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {
    private Enum amount;
    private Enum term;
    private Integer monthlyPayment;
    private LocalDate rate;
    private String psk;
    private EmploymentDto isInsuranceEnabled;
    private String isSalaryClient;
}
