package reports.formatters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reports.base.Report;
import reports.base.ReportFormatter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportFormatterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    static class DummyReport extends Report {
        public DummyReport() {
            this.title = "Dummy Report";
        }

        @Override
        protected void fetchData(LocalDate start, LocalDate end) {
            // no-op
        }

        @Override
        protected void processData() {
            // no-op
        }
    }

    private Report dummyReport;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        dummyReport = new DummyReport();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void cliFormatter_ShouldPrintReportTitle() {
        ReportFormatter formatter = new CLIReportFormatter();
        formatter.format(dummyReport);

        String output = outContent.toString();
        assertTrue(output.contains("Dummy Report"), "CLI output should include the report title");
    }

    @Test
    void excelFormatter_ShouldPrintNotImplementedMessage() {
        ReportFormatter formatter = new ExcelReportFormatter();
        formatter.format(dummyReport);

        String output = outContent.toString().trim();
        assertEquals("Excel export not implemented yet.", output);
    }

    @Test
    void pdfFormatter_ShouldPrintNotImplementedMessage() {
        ReportFormatter formatter = new PDFReportFormatter();
        formatter.format(dummyReport);

        String output = outContent.toString().trim();
        assertEquals("PDF export not implemented yet.", output);
    }
}
