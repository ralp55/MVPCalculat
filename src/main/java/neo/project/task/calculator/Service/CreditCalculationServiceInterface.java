package neo.project.task.calculator.Service;

import neo.project.task.calculator.DTO.CreditDto;
import neo.project.task.calculator.DTO.ScoringDataDto;

public interface CreditCalculationServiceInterface {
    CreditDto calculateCredit(ScoringDataDto scoring);
}
