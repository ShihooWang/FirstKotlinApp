package cc.bodyplus.health.mvp.module;

/**
 * Created by rui.gao on 2018-05-17.
 */

public class RecordEcgListBean {
    private String tags;
    private String record;
    private String diagnosisType;
    private String timestamp;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
