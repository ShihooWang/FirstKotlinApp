package cc.bodyplus.health.mvp.module;

import java.io.Serializable;

/**
 * Created by rui.gao on 2018-05-17.
 */

public class ReportEcgListBean implements Serializable{
    private String ecgName;
    private String timestamp;

    public String getEcgName() {
        return ecgName;
    }

    public void setEcgName(String ecgName) {
        this.ecgName = ecgName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
