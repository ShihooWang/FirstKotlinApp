package cc.bodyplus.health.mvp.module;

import java.io.Serializable;

/**
 * Created by Shihu.Wang on 2017/11/28.
 * Email shihu.wang@bodyplus.cc
 *
 * ECG波形图数据
 */

public class BPECGOriginal implements Serializable {

    public int[] values;    // 心电的原始数据
    public int status;      // 每秒的状态 正常/异常 用来循环记录正常一段波形的标示
    public int[] acc;      // 加速度原始数据
    public int loff;       // 脱落状态
    public String extend;   // 扩展
}
