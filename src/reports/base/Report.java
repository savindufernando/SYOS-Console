package reports.base;

import java.time.LocalDate;

/**
 * Abstract Report (Template Method Pattern).
 * Defines the skeleton of report generation.
 */
public abstract class Report {
    protected String title;

    public final void generate(LocalDate start, LocalDate end, ReportFormatter formatter) {
        fetchData(start, end);
        processData();
        formatter.format(this);
    }

    protected abstract void fetchData(LocalDate start, LocalDate end);
    protected abstract void processData();

    public String getTitle() {
        return title;
    }
}
