package reports.decorators;

import reports.base.Report;
import reports.base.ReportFormatter;

public class SummaryDecorator implements ReportFormatter {
    private final ReportFormatter wrapped;

    public SummaryDecorator(ReportFormatter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void format(Report report) {
        wrapped.format(report);
        System.out.println("[Summary Section Here]");
    }
}
