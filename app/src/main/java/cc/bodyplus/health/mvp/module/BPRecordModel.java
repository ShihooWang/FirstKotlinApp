package cc.bodyplus.health.mvp.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shihu.Wang on 2017/11/28.
 * Email shihu.wang@bodyplus.cc
 *
 * ECG记录实例
 */

public class BPRecordModel implements Serializable {
    public int type = 0; // 手动记录的
    public int avgHR;    // 平均心率
    public long stamp;    // 该段心电数据的起始时间戳
    public ArrayList<Integer> desc = new ArrayList<>();     // 诊断状态 20长度数组
//    public ArrayList<BPECGOriginal> ecgOriginals = new ArrayList<>(); // Ecg数据集合
    public float gain;     // 增益
    public float speed;    // 走速
    public int[] dealArr;  // 滤波后的数据
    public ArrayList<Integer> tag = new ArrayList<>();  // 状况描述（选择标签）
    public String edit;  // 状况描述（手动输入）

    @Override
    public String toString() {
        return "avgHr:"+avgHR
                +",stamp:"+stamp
                +",gain:"+gain
                +",speed:"+speed
                +",edit:"+edit
                +",desc:"+ desc.toString()
                +",tag:"+ tag.toString()
                +",dealArr:"+ Arrays.toString(dealArr);
    }
}
