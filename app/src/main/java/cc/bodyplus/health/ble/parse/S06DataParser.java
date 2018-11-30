package cc.bodyplus.health.ble.parse;

import java.util.ArrayList;

import cc.bodyplus.health.ble.bean.OriginalFrameData;
import cc.bodyplus.health.ble.bean.SimpleFrameData;

import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_EXTEND_AND_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_EXTEND_NO_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_FIRST_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_SIMPLE_EXTEND_AND_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_SIMPLE_EXTEND_NO_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_SIMPLE_NO_RE_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_SIMPLE_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_STANDARD_AND_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_STANDARD_EXTEND_AND_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_STANDARD_EXTEND_NO_RES_BYTE;
import static cc.bodyplus.health.ble.parse.M2MegicType.MAGIC_STANDARD_NO_RES_BYTE;
import static cc.bodyplus.health.ble.utils.BleConst.ORGINAL_ACC;
import static cc.bodyplus.health.ble.utils.BleConst.ORGINAL_BREATH;
import static cc.bodyplus.health.ble.utils.BleConst.ORGINAL_ECG;

/**
 * Created by Shihu.Wang on 2017/12/12.
 * Email shihu.wang@bodyplus.cc
 */

public class S06DataParser {

    public static void parserData(byte[] data,S06DataParserCallBack callBack){
        if (data[0] == MAGIC_FIRST_BYTE){
            if (data[1]==MAGIC_SIMPLE_NO_RE_BYTE || data[1]==MAGIC_SIMPLE_RES_BYTE) {  //简化帧 无应答||需应答
                SimpleFrameData simpleFrameData = new SimpleFrameData();
                simpleFrameData.setHeart(data[3]);
                simpleFrameData.setBreath(data[4]);
                callBack.simpleFrameData(simpleFrameData);
                return;
            }else if (data[1]==MAGIC_SIMPLE_EXTEND_NO_RES_BYTE || data[1]==MAGIC_SIMPLE_EXTEND_AND_RES_BYTE){ //简化帧(含扩展) 无应答||需应答
                SimpleFrameData simpleFrameData = new SimpleFrameData();
                simpleFrameData.setHeart(data[3]);
                simpleFrameData.setBreath(data[4]);
                int extendLength = data.length-6;
                byte[] extendData = new byte[extendLength];
                System.arraycopy(data,6,extendData,0,extendLength);
                parserExtendData(simpleFrameData,extendData,data[5]);
                callBack.simpleFrameData(simpleFrameData);
                return;
            }


            /**
             *  扩展帧
             *  帧头	    扩展ID	变量0	变量1	…
             3byte	1byte	1byte	1byte	…
             标准帧
             帧头	类型ID	长度	数据(含扩展)	 数据     CRC
             3byte	1byte	2byte	        Xbyte	1byte

             */
            OriginalFrameData originalFrameData = new OriginalFrameData();
            originalFrameData.setFrameCount(data[2]&0xff);
            byte extendId = 0;
            if (data[1]==MAGIC_EXTEND_NO_RES_BYTE || data[1]==MAGIC_EXTEND_AND_RES_BYTE){  // 扩展帧 无应答||需应答
                extendId = data[3];
                int extendLength = data.length-4;
                byte[] extendData = new byte[extendLength];
                System.arraycopy(data,4,extendData,0,extendLength);
                parserExtendData(originalFrameData,extendData,extendId);

            }else if (data[1]==MAGIC_STANDARD_NO_RES_BYTE || data[1]==MAGIC_STANDARD_AND_RES_BYTE){  // 标准帧 无应答||需应答
//                BleEcgLogUtils.handleECGLogData(value);
                byte[] ecg = new byte[ORGINAL_ECG]; // 500
                byte[] acc = new byte[ORGINAL_ACC]; // 150
                byte[] breath = new byte[ORGINAL_BREATH]; // 50
                System.arraycopy(data,3+1+2+ORGINAL_ECG+ORGINAL_ACC,breath,0,ORGINAL_BREATH);
                originalFrameData.setBreath(switchByteToInt(breath));

                System.arraycopy(data,3+1+2,ecg,0,ORGINAL_ECG);
                System.arraycopy(data,3+1+2+ORGINAL_ECG,acc,0,ORGINAL_ACC);
                originalFrameData.setEcg(switchByteToInt(ecg));
                originalFrameData.setAcc(switchByteToInt(acc));
            }else if (data[1]==MAGIC_STANDARD_EXTEND_NO_RES_BYTE || data[1]==MAGIC_STANDARD_EXTEND_AND_RES_BYTE){  // 标准帧(含扩展) 无应答||需应答
//                BleEcgLogUtils.handleECGLogData(value);
                byte[] ecg = new byte[ORGINAL_ECG];
                byte[] acc = new byte[ORGINAL_ACC];

                int extendIndex = 0;

                extendIndex = 3+1+2+ORGINAL_ECG+ORGINAL_ACC+ORGINAL_BREATH;
                extendId = data[extendIndex];

                byte[] breath = new byte[ORGINAL_BREATH];
                System.arraycopy(data,3+1+2+ORGINAL_ECG+ORGINAL_ACC,breath,0,ORGINAL_BREATH);
                originalFrameData.setBreath(switchByteToInt(breath));

                System.arraycopy(data,3+1+2,ecg,0,ORGINAL_ECG);
                System.arraycopy(data,3+1+2+ORGINAL_ECG,acc,0,ORGINAL_ACC);
                originalFrameData.setEcg(switchByteToInt(ecg));
                originalFrameData.setAcc(switchByteToInt(acc));

                int extendLength = data.length-extendIndex-2;
                byte[] extendData = new byte[extendLength];
                System.arraycopy(data,extendIndex+1,extendData,0,extendLength);
                parserExtendData(originalFrameData,extendData,extendId);
            }
            callBack.frameData(originalFrameData,0);
        }
    }

    private static int[] switchByteToInt(byte[] values) {
        int[] data = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            data[i] = values[i];
        }
        return data;
    }
    private static void parserExtendData(OriginalFrameData originalFrameData,byte[] extendData,byte extendId){
        ArrayList<Byte> dataList = new ArrayList<>();
        for (byte data : extendData) {
            dataList.add(data);
        }
        if ((extendId & 0x01)==1){ // 步数
            originalFrameData.setTotalStep(dataList.remove(0));
        }
        if (((extendId>>1) & 0x01)==1){ //充电状态
            originalFrameData.setChargePowerStatus(dataList.remove(0));
        }
        if (((extendId>>2) & 0x01)==1){ // 电池电量
            originalFrameData.setPowerLevel(dataList.remove(0));
        }
        if (((extendId>>3) & 0x01)==1){ // 脱落状态
            originalFrameData.setLeadOffStatus(dataList.remove(0));
        }
        if (((extendId>>4) & 0x01)==1){ // 心跳
            originalFrameData.setHeartBeat(dataList.remove(0));
        }
        if (((extendId>>5) & 0x01)==1){ // Core位置
            originalFrameData.setCoreModel(dataList.remove(0));
        }
        if (((extendId>>6) & 0x01)==1){ // 第二扩展数据
        }
        if (((extendId>>7) & 0x01)==1){ // 步频
            originalFrameData.setStepFrequen(dataList.remove(0));
        }
    }

    private static void parserExtendData(SimpleFrameData simpleFrameData,byte[] extendData,byte extendId){
        ArrayList<Byte> dataList = new ArrayList<>();
        for (byte data : extendData) {
            dataList.add(data);
        }
        if ((extendId & 0x01)==1){ // 步数
            simpleFrameData.setTotalStep(dataList.remove(0));
        }
        if (((extendId>>1) & 0x01)==1){ //充电状态
            simpleFrameData.setChargePowerStatus(dataList.remove(0));
        }
        if (((extendId>>2) & 0x01)==1){ // 电池电量
            simpleFrameData.setPowerLevel(dataList.remove(0));
        }
        if (((extendId>>3) & 0x01)==1){ // 脱落状态
            simpleFrameData.setLeadOffStatus(dataList.remove(0));
        }
        if (((extendId>>4) & 0x01)==1){ // 心跳
            simpleFrameData.setHeartBeat(dataList.remove(0));
        }
        if (((extendId>>5) & 0x01)==1){ // Core位置
            simpleFrameData.setCoreModel(dataList.remove(0));
        }
        if (((extendId>>6) & 0x01)==1){ // 第二扩展数据

        }
        if (((extendId>>7) & 0x01)==1){ // 步频
            simpleFrameData.setStepFrequen(dataList.remove(0));
        }
    }

    public interface S06DataParserCallBack{
        void frameData(OriginalFrameData originalFrameData, int error);
        void simpleFrameData(SimpleFrameData simpleFrameData);
    }
}
