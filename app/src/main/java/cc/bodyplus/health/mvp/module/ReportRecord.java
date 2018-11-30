package cc.bodyplus.health.mvp.module;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by kate.chen on 2017/12/26.
 * chunyan.chen@bodyplus.cc
 */

public class ReportRecord implements Serializable {

        private ArrayList<HistoryBean> dataList = new ArrayList<>();
        private String date;

        public ArrayList<HistoryBean> getDataList() {
                return dataList;
        }

        public void setDataList(ArrayList<HistoryBean> dataList) {
                this.dataList = dataList;
        }

        public String getDate() {
                return date;
        }

        public void setDate(String date) {
                this.date = date;
        }
}
