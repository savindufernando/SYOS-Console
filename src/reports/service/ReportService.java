package reports.service;

import reports.base.Report;
import reports.base.ReportFormatter;

import java.time.LocalDate;

public class ReportService {
    public void generateReport(Report report, ReportFormatter formatter,
                               LocalDate start, LocalDate end) {
        report.generate(start, end, formatter);
    }
}
