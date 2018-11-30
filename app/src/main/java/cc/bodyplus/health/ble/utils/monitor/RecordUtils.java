package cc.bodyplus.health.ble.utils.monitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cc.bodyplus.health.ble.bean.OriginalFrameData;
import cc.bodyplus.health.ble.parse.M2MegicType;
import cc.bodyplus.health.mvp.module.BPECGOriginal;

import static cc.bodyplus.health.ble.utils.BleConst.DATA_LENGTH;
import static cc.bodyplus.health.ble.utils.BleConst.ORGINAL_ACC;
import static cc.bodyplus.health.ble.utils.BleConst.ORGINAL_ECG;


/**
 * Created by Shihu.Wang on 2017/11/30.
 * Email shihu.wang@bodyplus.cc
 */

public class RecordUtils {



    public static String generateHourAndMin(long time){
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 16);
        return hour;
    }

    /**
     * 生成标准帧数据
     * @param originalFrameData
     * @return
     */
    public static BPECGOriginal generateFrameData(OriginalFrameData originalFrameData){
        if (originalFrameData == null){
            return null;
        }
        BPECGOriginal ecgOriginal = new BPECGOriginal();
        if (originalFrameData.getEcg()==null || originalFrameData.getEcg().length!= M2MegicType.ECG_DATA_LENGTH){
            return generateErrorBPECGOriginal();
        }else {
            ecgOriginal.values = originalFrameData.getEcg();
        }
        if (originalFrameData.getAcc()!=null && originalFrameData.getAcc().length==M2MegicType.ACC_DATA_LENGTH){
            ecgOriginal.acc = originalFrameData.getAcc();
        }
        ecgOriginal.loff = originalFrameData.getLeadOffStatus();
        ecgOriginal.extend = " ";
        return ecgOriginal;
    }


    public static ArrayList<BPECGOriginal> generateEcgOraiginals(LimitEcgQueue<BPECGOriginal> ecgBuff){
        ArrayList<BPECGOriginal> result = new ArrayList<>();
        if (ecgBuff.getQueueSize() == 0){
            return result;
        }
        for (int i = 0; i< DATA_LENGTH; i++) {
            BPECGOriginal bpecgOriginal = ecgBuff.get(i);
            if (bpecgOriginal == null) {
                bpecgOriginal = generateErrorBPECGOriginal();
            }
            result.add(bpecgOriginal);
        }
        return result;
    }

    public static BPECGOriginal generateErrorBPECGOriginal(){
        BPECGOriginal ecgOriginal = new BPECGOriginal();
        ecgOriginal.status = 0;
        ecgOriginal.values = makeErrorData(ORGINAL_ECG);
        ecgOriginal.acc = makeErrorData(ORGINAL_ACC);
        ecgOriginal.extend = "";
        ecgOriginal.loff = 0;
        return ecgOriginal;
    }

    private static int[] makeErrorData(int length){
        int[] data = new int[length];
        for (int i=0;i<length;i++){
            data[i] = 0xff;
        }
        return data;
    }

    public static ArrayList<Integer> generateArrayList(int[] data){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0;i<data.length;i++){
            list.add(data[i]);
        }
        return list;
    }

}

