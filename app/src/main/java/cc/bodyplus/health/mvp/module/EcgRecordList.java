package cc.bodyplus.health.mvp.module;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shihoo.wang on 2018/10/25.
 * Email shihu.wang@bodyplus.cc
 */
public class EcgRecordList implements Serializable {

    private String nextPage;
    private ArrayList<RecordEcgListBean> dataList;

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public ArrayList<RecordEcgListBean> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<RecordEcgListBean> dataList) {
        this.dataList = dataList;
    }
}
