package life.offonoff.ab.application.event.report;

public interface ReportEvent {
    String getReportedContent();
    int getReportCount();
    ReportType getReportType();
}
