package neo.project.task.calculator.Controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import neo.project.task.calculator.DTO.LoanOfferDto;
import neo.project.task.calculator.DTO.LoanStatementRequestDto;
import neo.project.task.calculator.Service.LoanCalculatorServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/calculator")
@Tag(name = "Loan Calculator API", description = "API для расчёта возможности кредитации")
public class CalculatorController {
    @Autowired
    private LoanCalculatorServiceInterface calculatorService;
    @Operation(
            summary = "Получение кредитных предложений",
            description = "Вычисляет список возможных кредитных предложений по параметрам клиента.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanOfferDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные",
                            content = @Content
                    )
            }
    )
    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@RequestBody LoanStatementRequestDto request) {
        log.info("Received loan request: {}", request);
        List<LoanOfferDto> offers = calculatorService.processLoanRequest(request);
        log.info("Successfully generated {} loan offers", offers.size());
        return ResponseEntity.ok(offers);
    }
}
