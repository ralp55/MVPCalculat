package neo.project.task.calculator.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class EmploymentDto {
    @Schema(description = "Статус занятости", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работодателя", example = "1234567890")
    private String employerINN;

    @Schema(description = "Ежемесячная зарплата", example = "100000")
    private BigDecimal salary;

    @Schema(description = "Должность", example = "MIDDLE_MANAGER")
    private Position position;

    @Schema(description = "Общий стаж (в месяцах)", example = "120")
    private Integer workExperienceTotal;

    @Schema(description = "Стаж на текущем месте (в месяцах)", example = "24")
    private Integer workExperienceCurrent;
}

