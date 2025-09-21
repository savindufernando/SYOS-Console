package reports.decorators;

import reports.base.Report;
import reports.base.ReportFormatter;

public class HeaderFooterDecorator implements ReportFormatter {
    private final ReportFormatter wrapped;

    public HeaderFooterDecorator(ReportFormatter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void format(Report report) {
        System.out.println("=== SYOS Store Report ===");
        wrapped.format(report);
        System.out.println("=== End of Report ===");
    }
}
