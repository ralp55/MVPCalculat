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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CreditDto calculate(@RequestBody ScoringDataDto request) {
        return calculationService.calculateCredit(request);
    }
}
