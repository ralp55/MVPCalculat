package neo.project.task.calculator.DTO;
import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private Enum theme;
    private Long statementId;
    private String text;
}
