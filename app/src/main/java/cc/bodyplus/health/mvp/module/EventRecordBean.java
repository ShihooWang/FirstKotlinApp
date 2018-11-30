package cc.bodyplus.health.mvp.module;

import java.io.Serializable;

/**
 * Created by rui.gao on 2018-05-17.
 */
public class EventRecordBean implements Serializable{
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
