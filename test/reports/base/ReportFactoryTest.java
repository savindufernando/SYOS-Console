package reports.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reports.concrete.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportFactoryTest {

    // ---------- ReportFactory Tests ----------
    @Test
    void shouldReturnDailySalesReport_WhenChoiceIs1() {
        Report report = ReportFactory.createReport(1);
        assertTrue(report instanceof DailySalesReport);
    }

    @Test
    void shouldReturnReshelvedItemsReport_WhenChoiceIs2() {
        Report report = ReportFactory.createReport(2);
        assertTrue(report instanceof ReshelvedItemsReport);
    }

    @Test
    void shouldReturnReorderLevelReport_WhenChoiceIs3() {
        Report report = ReportFactory.createReport(3);
        assertTrue(report instanceof ReorderLevelReport);
    }

    @Test
    void shouldReturnStockReport_WhenChoiceIs4() {
        Report report = ReportFactory.createReport(4);
        assertTrue(report instanceof StockReport);
    }

    @Test
    void shouldReturnBillReport_WhenChoiceIs5() {
        Report report = ReportFactory.createReport(5);
        assertTrue(report instanceof BillReport);
    }

    @Test
    void shouldThrowException_WhenChoiceIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ReportFactory.createReport(99));
    }


    // ---------- Report.generate() Tests ----------
    static class DummyReport extends Report {
        boolean fetchCalled = false;
        boolean processCalled = false;
        boolean formatCalled = false;

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

    private DummyReport dummyReport;
    private DummyFormatter dummyFormatter;

    @BeforeEach
    void setUp() {
        dummyReport = new DummyReport();
        dummyFormatter = new DummyFormatter();
    }

    @Test
    void generate_ShouldCallFetchProcessAndFormat() {
        dummyReport.generate(LocalDate.now(), LocalDate.now(), dummyFormatter);

        assertTrue(dummyReport.fetchCalled, "fetchData should be called");
        assertTrue(dummyReport.processCalled, "processData should be called");
        assertTrue(dummyFormatter.formatCalled, "formatter.format should be called");
    }
}
