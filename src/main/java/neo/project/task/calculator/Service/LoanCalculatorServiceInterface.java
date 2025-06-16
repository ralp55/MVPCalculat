package neo.project.task.calculator.Service;

import neo.project.task.calculator.DTO.LoanOfferDto;
import neo.project.task.calculator.DTO.LoanStatementRequestDto;

import java.util.List;

public interface LoanCalculatorServiceInterface {
    List<LoanOfferDto> processLoanRequest(LoanStatementRequestDto request);
}
