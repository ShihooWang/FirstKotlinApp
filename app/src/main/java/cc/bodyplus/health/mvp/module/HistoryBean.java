package cc.bodyplus.health.mvp.module;

/**
 * Created by rui.gao on 2018-05-17.
 */

public class HistoryBean {
    private String detectId;
    private String detectTime;
    private String isView;
    private String isCheck;
    private String detectDate;
    private String userID;
    private String patientView;
    private String reportUrl;

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getDetectId() {
        return detectId;
    }

    public void setDetectId(String detectId) {
        this.detectId = detectId;
    }

    public String getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(String detectTime) {
        this.detectTime = detectTime;
    }

    public String getIsView() {
        return isView;
    }

    public void setIsView(String isView) {
        this.isView = isView;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getDetectDate() {
        return detectDate;
    }

    public void setDetectDate(String detectDate) {
        this.detectDate = detectDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPatientView() {
        return patientView;
    }

    public void setPatientView(String patientView) {
        this.patientView = patientView;
    }
}
