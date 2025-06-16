package neo.project.task.calculator.Service;

public class LoanApplicationRejectedException extends RuntimeException {
    public LoanApplicationRejectedException(String message) {
        super(message);
    }
}
