package neo.project.task.calculator.DTO;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
}
