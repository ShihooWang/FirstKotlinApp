package cc.bodyplus.health.mvp.module;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rui.gao on 2018-05-17.
 */
public class ReportInfo implements Serializable{
    private String avgHr;
    private String maxHr;
    private String minHr;
    private String brady;
    private String detectTime;
    private String tachy;
    private String diagnosisAnalysis;
    private String diagnosisDoctor;
    private String chiefAnalysis;
    private String chiefDoctor;
    private ArrayList<EventList> eventRecord;
    private ArrayList<RecordEcgListBean> recordEcgList;
    private ArrayList<ReportEcgListBean> reportEcgList;

    public String getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(String avgHr) {
        this.avgHr = avgHr;
    }

    public String getMaxHr() {
        return maxHr;
    }

    public void setMaxHr(String maxHr) {
        this.maxHr = maxHr;
    }

    public String getMinHr() {
        return minHr;
    }

    public void setMinHr(String minHr) {
        this.minHr = minHr;
    }

    public String getBrady() {
        return brady;
    }

    public void setBrady(String brady) {
        this.brady = brady;
    }

    public String getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(String detectTime) {
        this.detectTime = detectTime;
    }

    public String getTachy() {
        return tachy;
    }

    public void setTachy(String tachy) {
        this.tachy = tachy;
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


    public ArrayList<RecordEcgListBean> getRecordEcgList() {
        return recordEcgList;
    }

    public void setRecordEcgList(ArrayList<RecordEcgListBean> recordEcgList) {
        this.recordEcgList = recordEcgList;
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
