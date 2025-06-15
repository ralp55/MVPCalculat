package neo.project.task.calculator.Controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import neo.project.task.calculator.DTO.CreditDto;
import neo.project.task.calculator.DTO.LoanOfferDto;
import neo.project.task.calculator.DTO.ScoringDataDto;
import neo.project.task.calculator.Service.CreditCalculationService;
import neo.project.task.calculator.Service.LoanApplicationRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequestMapping("/calculator")
@Tag(name = "Credit Calculator API", description = "API для расчёта кредитных условий")
public class CreditController {
    private final CreditCalculationService calculationService;

    public CreditController(CreditCalculationService calculationService) {
        this.calculationService = calculationService;
    }
    @Operation(
            summary = "Рассчитать кредитные предложения",
            description = "Принимает данные запроса на кредит и возвращает список кредитных предложений с различными условиями",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список кредитных предложений",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanOfferDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные входные данные",
                            content = @Content)
            }
    )
    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calculate(@RequestBody ScoringDataDto request) {
        log.info("Received scoring request: {}", request);
        CreditDto credit = calculationService.calculateCredit(request);
        log.info("Successfully calculated credit: {}", credit);
        return ResponseEntity.ok(credit);
    }
}
