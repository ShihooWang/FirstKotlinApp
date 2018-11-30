package cc.bodyplus.health.mvp.module;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rui.gao on 2018-05-17.
 */
public class ReportInfo2 implements Serializable{
    private String diagnosisAnalysis;
    private String diagnosisDoctor;
    private String chiefAnalysis;
    private String chiefDoctor;
    private ArrayList<EventList> eventRecord;
    private ArrayList<ReportEcgListBean> reportEcgList;
    private String reportUrl;

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getDiagnosisAnalysis() {
        return diagnosisAnalysis;
    }

    public void setDiagnosisAnalysis(String diagnosisAnalysis) {
        this.diagnosisAnalysis = diagnosisAnalysis;
    }

    public String getChiefAnalysis() {
        return chiefAnalysis;
    }

    public void setChiefAnalysis(String chiefAnalysis) {
        this.chiefAnalysis = chiefAnalysis;
    }


    public ArrayList<ReportEcgListBean> getReportEcgList() {
        return reportEcgList;
    }

    public void setReportEcgList(ArrayList<ReportEcgListBean> reportEcgList) {
        this.reportEcgList = reportEcgList;
    }

    public String getDiagnosisDoctor() {
        return diagnosisDoctor;
    }

    public void setDiagnosisDoctor(String diagnosisDoctor) {
        this.diagnosisDoctor = diagnosisDoctor;
    }

    public String getChiefDoctor() {
        return chiefDoctor;
    }

    public void setChiefDoctor(String chiefDoctor) {
        this.chiefDoctor = chiefDoctor;
    }

    public ArrayList<EventList> getEventRecord() {
        return eventRecord;
    }

    public void setEventRecord(ArrayList<EventList> eventRecord) {
        this.eventRecord = eventRecord;
    }
}
