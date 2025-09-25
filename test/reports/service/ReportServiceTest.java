package reports.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reports.base.Report;
import reports.base.ReportFormatter;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService service;
    private DummyReport dummyReport;
    private DummyFormatter dummyFormatter;
    private LocalDate startDate;
    private LocalDate endDate;

    static class DummyReport extends Report {
        boolean fetchCalled = false;
        boolean processCalled = false;

        @Override
        protected void fetchData(LocalDate start, LocalDate end) {
            fetchCalled = true;
        }

        @Override
        protected void processData() {
            processCalled = true;
        }
    }

    static class DummyFormatter implements ReportFormatter {
        boolean formatCalled = false;

        @Override
        public void format(Report report) {
            formatCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        service = new ReportService();
        dummyReport = new DummyReport();
        dummyFormatter = new DummyFormatter();
        startDate = LocalDate.of(2025, 1, 1);
        endDate = LocalDate.of(2025, 1, 31);
    }

    @Test
    void generateReport_ShouldCallReportGenerateFlow() {
        service.generateReport(dummyReport, dummyFormatter, startDate, endDate);

        assertTrue(dummyReport.fetchCalled, "fetchData() should be called");
        assertTrue(dummyReport.processCalled, "processData() should be called");
        assertTrue(dummyFormatter.formatCalled, "formatter.format() should be called");
    }
}
