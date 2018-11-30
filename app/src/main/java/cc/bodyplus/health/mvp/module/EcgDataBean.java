package cc.bodyplus.health.mvp.module;

import java.io.Serializable;

/**
 * Created by rui.gao on 2018-05-18.
 */

public class EcgDataBean implements Serializable{
    private String ecg;
    private String avgHr;

    public String getEcg() {
        return ecg;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    public String getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(String avgHr) {
        this.avgHr = avgHr;
    }
}
