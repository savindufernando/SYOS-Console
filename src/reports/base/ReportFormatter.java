package reports.base;

/**
 * Bridge Pattern – separates report logic from formatting.
 */
public interface ReportFormatter {
    void format(Report report);
}
