package reports.decorators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reports.base.Report;
import reports.base.ReportFormatter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportDecoratorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    static class DummyReport extends Report {
        public DummyReport() {
            this.title = "Dummy Report";
        }

        @Override
        protected void fetchData(LocalDate start, LocalDate end) { }
        @Override
        protected void processData() { }
    }

    static class DummyFormatter implements ReportFormatter {
        @Override
        public void format(Report report) {
            System.out.println("Base Formatting: " + report.getTitle());
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
        outContent.reset();
    }

    @Test
    void headerFooterDecorator_ShouldAddHeaderAndFooter() {
        ReportFormatter formatter = new HeaderFooterDecorator(new DummyFormatter());
        formatter.format(dummyReport);

        String output = outContent.toString();

        assertTrue(output.contains("=== SYOS Store Report ==="), "Header should be present");
        assertTrue(output.contains("Base Formatting: Dummy Report"), "Wrapped formatter output should be present");
        assertTrue(output.contains("=== End of Report ==="), "Footer should be present");
    }

    @Test
    void summaryDecorator_ShouldAppendSummarySection() {
        ReportFormatter formatter = new SummaryDecorator(new DummyFormatter());
        formatter.format(dummyReport);

        String output = outContent.toString();

        assertTrue(output.contains("Base Formatting: Dummy Report"), "Wrapped formatter output should be present");
        assertTrue(output.contains("[Summary Section Here]"), "Summary should be appended");
    }
}
