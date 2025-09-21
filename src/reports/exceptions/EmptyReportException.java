package reports.exceptions;

public class EmptyReportException extends RuntimeException {
    public EmptyReportException(String msg) {
        super(msg);
    }
}
