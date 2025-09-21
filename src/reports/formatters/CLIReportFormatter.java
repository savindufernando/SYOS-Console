package reports.formatters;

import reports.base.Report;
import reports.base.ReportFormatter;

public class CLIReportFormatter implements ReportFormatter {
    @Override
    public void format(Report report) {
        // For CLI we already print in processData()
        System.out.println("\n=== " + report.getTitle() + " Generated ===\n");
    }
}
