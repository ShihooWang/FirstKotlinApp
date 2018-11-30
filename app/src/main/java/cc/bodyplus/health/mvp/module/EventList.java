package cc.bodyplus.health.mvp.module;

import java.io.Serializable;

/**
 * Created by rui.gao on 2018-05-18.
 */

public class EventList implements Serializable{
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
