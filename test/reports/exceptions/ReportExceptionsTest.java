package reports.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportExceptionsTest {

    @Test
    void emptyReportException_ShouldStoreMessage() {
        String msg = "No data found for report";
        EmptyReportException ex = new EmptyReportException(msg);

        assertEquals(msg, ex.getMessage());
    }

    @Test
    void reportGenerationException_ShouldStoreMessage() {
        String msg = "Failed to generate report";
        ReportGenerationException ex = new ReportGenerationException(msg);

        assertEquals(msg, ex.getMessage());
    }

    @Test
    void emptyReportException_ShouldBeThrownAndCaught() {
        assertThrows(EmptyReportException.class, () -> {
            throw new EmptyReportException("trigger");
        });
    }

    @Test
    void reportGenerationException_ShouldBeThrownAndCaught() {
        assertThrows(ReportGenerationException.class, () -> {
            throw new ReportGenerationException("trigger");
        });
    }
}
