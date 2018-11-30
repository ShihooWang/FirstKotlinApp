package cc.bodyplus.health.ble.parse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shihu.wang on 2017/3/17.
 * Email shihu.wang@bodyplus.cc
 */
public class BleCmdConfig {
    /**
     * BLE命令参数
     */
//时间设置
    public static final short BLE_CMD_TIME = (short) 4;
    /**
     * The constant BLE_CMD_UPLOAD_DATA.
     */
//数据上传总开关
    public static final short BLE_CMD_UPLOAD_DATA = (short) 12;
    /**
     * The constant BLE_CMD_UPLOAD_DATA_EMG.
     */
//数据上传总开关
    public static final short BLE_CMD_UPLOAD_DATA_EMG = (short) 13;
    /**
     * The constant BLE_CMD_UPLOAD_DATA_ECG.
     */
//ECG数据上传总开关
    public static final short BLE_CMD_UPLOAD_DATA_ECG = (short) 14;
    /**
     * The constant BLE_CMD_USER_ID.
     */
//用户标示码
    public static final short BLE_CMD_USER_ID = (short) 40;
    /**
     * The constant BLE_CMD_PASSWORD_CHECK.
     */
//校验连接验证码
    public static final short BLE_CMD_PASSWORD_CHECK = (short) 48;
    /**
     * The constant BLE_CMD_POW_LEV.
     */
//电池电量
    public static final short BLE_CMD_POW_LEV = (short) 57;
    /**
     * The constant BLE_CMD_HW_VN.
     */
//硬件版本号
    public static final short BLE_CMD_HW_VN = (short) 64;
    /**
     * The constant BLE_CMD_SW_VN.
     */
//固件版本号
    public static final short BLE_CMD_SW_VN = (short) 66;
    /**
     * The constant BLE_CMD_CORE_SN.
     */
//SN码
    public static final short BLE_CMD_CORE_SN = (short) 68;
    /**
     * The constant BLE_CMD_CORE_MODE.
     */
//模块位置状态
    public static final short BLE_CMD_CORE_MODE = (short) 60;
    /**
     * The constant CORE_DATA_FLAG.
     */
//Core标定标示
    public static final short CORE_DATA_FLAG = (short) 78;
//    public static final short COREDATA_FLAG_SHIRT = (short)78;
//    //裤子标定标示
//    public static final short COREDATA_FLAG_PANTS = (short)79;

    /**
     * 心率数据
     */
    public static final int BLE_HEART_MESSAGE = 3;

    /**
     * 呼吸数据
     */
    @Deprecated
    public static final int BLE_BREATHING_MESSAGE = 4;


    /**
     * The constant DFU_FILE_SPLIT_SIZE.
     */
    public static final int DFU_FILE_SPLIT_SIZE = 248;

    /**
     * The constant COREDATA_EMGMAX_1.
     */
//每块肌肉的最大值与最小值
    public static final short COREDATA_EMGMAX_1 = (short) 80;
    /**
     * The constant COREDATA_EMGMIN_1.
     */
    public static final short COREDATA_EMGMIN_1 = (short) 84;
    /**
     * The constant COREDATA_EMGMAX_2.
     */
    public static final short COREDATA_EMGMAX_2 = (short) 88;
    /**
     * The constant COREDATA_EMGMIN_2.
     */
    public static final short COREDATA_EMGMIN_2 = (short) 92;
    /**
     * The constant COREDATA_EMGMAX_3.
     */
    public static final short COREDATA_EMGMAX_3 = (short) 96;
    /**
     * The constant COREDATA_EMGMIN_3.
     */
    public static final short COREDATA_EMGMIN_3 = (short) 100;
    /**
     * The constant COREDATA_EMGMAX_4.
     */
    public static final short COREDATA_EMGMAX_4 = (short) 104;
    /**
     * The constant COREDATA_EMGMIN_4.
     */
    public static final short COREDATA_EMGMIN_4 = (short) 108;
    /**
     * The constant COREDATA_EMGMAX_5.
     */
    public static final short COREDATA_EMGMAX_5 = (short) 112;
    /**
     * The constant COREDATA_EMGMIN_5.
     */
    public static final short COREDATA_EMGMIN_5 = (short) 116;
    /**
     * The constant COREDATA_EMGMAX_6.
     */
    public static final short COREDATA_EMGMAX_6 = (short) 120;
    /**
     * The constant COREDATA_EMGMIN_6.
     */
    public static final short COREDATA_EMGMIN_6 = (short) 124;
    /**
     * The constant COREDATA_EMGMAX_7.
     */
    public static final short COREDATA_EMGMAX_7 = (short) 128;
    /**
     * The constant COREDATA_EMGMIN_7.
     */
    public static final short COREDATA_EMGMIN_7 = (short) 132;
    /**
     * The constant COREDATA_EMGMAX_8.
     */
    public static final short COREDATA_EMGMAX_8 = (short) 136;
    /**
     * The constant COREDATA_EMGMIN_8.
     */
    public static final short COREDATA_EMGMIN_8 = (short) 140;
    /**
     * The constant COREDATA_EMGMAX_9.
     */
    public static final short COREDATA_EMGMAX_9 = (short) 144;
    /**
     * The constant COREDATA_EMGMIN_9.
     */
    public static final short COREDATA_EMGMIN_9 = (short) 148;
    /**
     * The constant COREDATA_EMGMAX_10.
     */
    public static final short COREDATA_EMGMAX_10 = (short) 152;
    /**
     * The constant COREDATA_EMGMIN_10.
     */
    public static final short COREDATA_EMGMIN_10 = (short) 156;
    /**
     * The constant COREDATA_EMGMAX_11.
     */
    public static final short COREDATA_EMGMAX_11 = (short) 160;
    /**
     * The constant COREDATA_EMGMIN_11.
     */
    public static final short COREDATA_EMGMIN_11 = (short) 164;
    /**
     * The constant COREDATA_EMGMAX_12.
     */
    public static final short COREDATA_EMGMAX_12 = (short) 168;
    /**
     * The constant COREDATA_EMGMIN_12.
     */
    public static final short COREDATA_EMGMIN_12 = (short) 172;
    /**
     * The constant COREDATA_EMGMAX_13.
     */
    public static final short COREDATA_EMGMAX_13 = (short) 176;
    /**
     * The constant COREDATA_EMGMIN_13.
     */
    public static final short COREDATA_EMGMIN_13 = (short) 180;
    /**
     * The constant COREDATA_EMGMAX_14.
     */
    public static final short COREDATA_EMGMAX_14 = (short) 184;
    /**
     * The constant COREDATA_EMGMIN_14.
     */
    public static final short COREDATA_EMGMIN_14 = (short) 188;
    /**
     * The constant COREDATA_EMGMAX_15.
     */
    public static final short COREDATA_EMGMAX_15 = (short) 192;
    /**
     * The constant COREDATA_EMGMIN_15.
     */
    public static final short COREDATA_EMGMIN_15 = (short) 196;
    /**
     * The constant COREDATA_EMGMAX_16.
     */
    public static final short COREDATA_EMGMAX_16 = (short) 200;
    /**
     * The constant COREDATA_EMGMIN_16.
     */
    public static final short COREDATA_EMGMIN_16 = (short) 204;
    /**
     * The constant COREDATA_EMGMAX_17.
     */
    public static final short COREDATA_EMGMAX_17 = (short) 208;
    /**
     * The constant COREDATA_EMGMIN_17.
     */
    public static final short COREDATA_EMGMIN_17 = (short) 212;
    /**
     * The constant COREDATA_EMGMAX_18.
     */
    public static final short COREDATA_EMGMAX_18 = (short) 216;
    /**
     * The constant COREDATA_EMGMIN_18.
     */
    public static final short COREDATA_EMGMIN_18 = (short) 220;
    /**
     * The constant COREDATA_EMGMAX_19.
     */
    public static final short COREDATA_EMGMAX_19 = (short) 224;
    /**
     * The constant COREDATA_EMGMIN_19.
     */
    public static final short COREDATA_EMGMIN_19 = (short) 228;
    /**
     * The constant COREDATA_EMGMAX_20.
     */
    public static final short COREDATA_EMGMAX_20 = (short) 232;
    /**
     * The constant COREDATA_EMGMIN_20.
     */
    public static final short COREDATA_EMGMIN_20 = (short) 236;
    /**
     * The constant COREDATA_EMGMAX_21.
     */
    public static final short COREDATA_EMGMAX_21 = (short) 240;
    /**
     * The constant COREDATA_EMGMIN_21.
     */
    public static final short COREDATA_EMGMIN_21 = (short) 244;
    /**
     * The constant COREDATA_EMGMAX_22.
     */
    public static final short COREDATA_EMGMAX_22 = (short) 248;
    /**
     * The constant COREDATA_EMGMIN_22.
     */
    public static final short COREDATA_EMGMIN_22 = (short) 252;
    /**
     * The constant COREDATA_EMGMAX_23.
     */
    public static final short COREDATA_EMGMAX_23 = (short) 256;
    /**
     * The constant COREDATA_EMGMIN_23.
     */
    public static final short COREDATA_EMGMIN_23 = (short) 260;
    /**
     * The constant COREDATA_EMGMAX_24.
     */
    public static final short COREDATA_EMGMAX_24 = (short) 264;
    /**
     * The constant COREDATA_EMGMIN_24.
     */
    public static final short COREDATA_EMGMIN_24 = (short) 268;
    /**
     * The constant COREDATA_EMGMAX_25.
     */
    public static final short COREDATA_EMGMAX_25 = (short) 272;
    /**
     * The constant COREDATA_EMGMIN_25.
     */
    public static final short COREDATA_EMGMIN_25 = (short) 276;
    /**
     * The constant COREDATA_EMGMAX_26.
     */
    public static final short COREDATA_EMGMAX_26 = (short) 280;
    /**
     * The constant COREDATA_EMGMIN_26.
     */
    public static final short COREDATA_EMGMIN_26 = (short) 284;
    /**
     * The constant COREDATA_EMGMAX_27.
     */
    public static final short COREDATA_EMGMAX_27 = (short) 288;
    /**
     * The constant COREDATA_EMGMIN_27.
     */
    public static final short COREDATA_EMGMIN_27 = (short) 292;


    /**
     * The constant DFUSTA_APP.
     */
// state
    public static final int DFUSTA_APP = 1;
    /**
     * The constant DFUSTA_BOOT_INIT.
     */
    public static final int DFUSTA_BOOT_INIT = 2;
    /**
     * The constant DFUSTA_BOOT_RECV.
     */
    public static final int DFUSTA_BOOT_RECV = 3;
    /**
     * The constant DFUSTA_MUST_INIT.
     */
    public static final int DFUSTA_MUST_INIT = 4;
    /**
     * The constant DFUSTA_MUST_RECV.
     */
    public static final int DFUSTA_MUST_RECV = 5;
    /**
     * The constant DFUSTA_BOOT_RVEND.
     */
    public static final int DFUSTA_BOOT_RVEND = 6;
    /**
     * The constant DFUSTA_UP_JUMP.
     */
    public static final int DFUSTA_UP_JUMP = 7;

    /**
     * BLE升级参数
     */
//stm32APP跳转到bootload
    public static final short DFU_APP2BOOT = 1;
    /**
     * The constant DFU_INIT.
     */
//初始化升级状态
    public static final short DFU_INIT = 2;
    /**
     * The constant DFU_SET.
     */
//设置长度，CRC，擦除Flash
    public static final short DFU_SET = 3;
    /**
     * The constant DFU_EXEUP.
     */
//执行升级
    public static final short DFU_EXEUP = 4;
    /**
     * The constant DFU_STATE.
     */
//查询DFU状态
    public static final short DFU_STATE = 5;

    /**
     * The constant DFU_BOOT2APP.
     */
//bootload跳转到stm32APP
    public static final short DFU_BOOT2APP = 6;

    private static Map<Short, Byte> mCommandPayloadLengthMap = new HashMap<>();

    static {
        mCommandPayloadLengthMap.put(BLE_CMD_TIME, (byte) 8);
        mCommandPayloadLengthMap.put(BLE_CMD_UPLOAD_DATA, (byte) 7);
        mCommandPayloadLengthMap.put(BLE_CMD_UPLOAD_DATA_ECG, (byte) 7);
        mCommandPayloadLengthMap.put(BLE_CMD_PASSWORD_CHECK, (byte) 8);
        mCommandPayloadLengthMap.put(BLE_CMD_POW_LEV, (byte) 1);
        mCommandPayloadLengthMap.put(BLE_CMD_HW_VN, (byte) 2);
        mCommandPayloadLengthMap.put(BLE_CMD_SW_VN, (byte) 2);
        mCommandPayloadLengthMap.put(BLE_CMD_CORE_SN, (byte) 8);
        mCommandPayloadLengthMap.put(DFU_APP2BOOT, (byte) 1);
        mCommandPayloadLengthMap.put(DFU_INIT, (byte) 1);
        mCommandPayloadLengthMap.put(DFU_SET, (byte) 8);
        mCommandPayloadLengthMap.put(DFU_EXEUP, (byte) 1);
        mCommandPayloadLengthMap.put(DFU_STATE, (byte) 1);
        mCommandPayloadLengthMap.put(BLE_CMD_USER_ID, (byte) 4);
        mCommandPayloadLengthMap.put(BLE_CMD_CORE_MODE, (byte) 1);
        mCommandPayloadLengthMap.put(CORE_DATA_FLAG, (byte) 1);
//        mCommandPayloadLengthMap.put(COREDATA_FLAG_PANTS, (byte)1);
        mCommandPayloadLengthMap.put(DFU_BOOT2APP, (byte) 1);

        mCommandPayloadLengthMap.put(M2CmdConfig.BLE_CMD_USER_NAME, (byte) 12);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLE_CMD_DFU_JUMP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLE_CMD_LED_STOP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLE_CMD_FILT_STORE_CNT_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLECMD_FILT_FISRT_JUMP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLECMD_FILT_WAVAMP_ALL_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(M2CmdConfig.BLECMD_FILT_WAVAMP_LAST_LENGTH, (byte) 1);
    }

    /**
     * Gets payload length by command.
     *
     * @param cmd the cmd
     * @return the payload length by command
     */
    public static byte getPayloadLengthByCommand(short cmd) {
        byte payloadLength = mCommandPayloadLengthMap.get(cmd);
        return payloadLength;
    }


    /**
     * BLE升级结果参数
     */
    public static final byte DFU_ERR_NONE = 0;
    /**
     * The constant DFU_ERR_POWERLEV.
     */
    public static final byte DFU_ERR_POWERLEV = 14;
    /**
     * The constant DFU_ERR_WRITE.
     */
    public static final byte DFU_ERR_WRITE = 1;


    /**
     * BLE 硬件版本号
     * 用来区分升级流程
     */
    public static final int BLEHARDWARE_SIX = 6;
    /**
     * The constant BLEHARDWARE_SEVEN.
     */
    public static final int BLEHARDWARE_SEVEN = 7;


    /**
     * The constant CMD_RED_CMD.
     */
//数据类别参数
    public static final int CMD_RED_CMD = 1;
    /**
     * The constant CMD_WRITE_CMD.
     */
    public static final int CMD_WRITE_CMD = 2;
    /**
     * The constant STM32_RED_CMD.
     */
    public static final int STM32_RED_CMD = 3;
    /**
     * The constant STM32_WRITE_CMD.
     */
    public static final int STM32_WRITE_CMD = 4;
    /**
     * The constant STM32_WRITE_DATA.
     */
    public static final int STM32_WRITE_DATA = 5;
    /**
     * The constant DATA_RED_CMD.
     */
    public static final int DATA_RED_CMD = 6;
    /**
     * The constant DATA_WRITE_CMD.
     */
    public static final int DATA_WRITE_CMD = 7;


    /**************
     * 前台 和 BLE服务 传递参数
     */
    public static final int INTENT_CMD_STOP_SERVICE = 1;

    /**
     * The constant INTENT_CMD_READ_CMD.
     */
//命令通道
    public static final int INTENT_CMD_READ_CMD = 2;
    /**
     * The constant INTENT_CMD_WRITE_CMD.
     */
    public static final int INTENT_CMD_WRITE_CMD = 3;
    /**
     * The constant INTENT_CMD_RESULT_READ_CMD.
     */
    public static final int INTENT_CMD_RESULT_READ_CMD = 4;
    /**
     * The constant INTENT_CMD_RESULT_WRITE_CMD.
     */
    public static final int INTENT_CMD_RESULT_WRITE_CMD = 5;

    /**
     * The constant INTENT_STM32_READ_CMD.
     */
//STM通道
    public static final int INTENT_STM32_READ_CMD = 6;
    /**
     * The constant INTENT_STM32_WRITE_CMD.
     */
    public static final int INTENT_STM32_WRITE_CMD = 7;
    /**
     * The constant INTENT_STM32_RESULT_READ_CMD.
     */
    public static final int INTENT_STM32_RESULT_READ_CMD = 8;
    /**
     * The constant INTENT_STM32_RESULT_WRITE_CMD.
     */
    public static final int INTENT_STM32_RESULT_WRITE_CMD = 9;
    /**
     * The constant INTENT_STM32_WRITE_DATA.
     */
    public static final int INTENT_STM32_WRITE_DATA = 10;
    /**
     * The constant INTENT_STM32_RESULT_WRITE_DATA.
     */
    public static final int INTENT_STM32_RESULT_WRITE_DATA = 11;

    /**
     * The constant INTENT_DATA_READ_CMD.
     */
//数据通道
    public static final int INTENT_DATA_READ_CMD = 12;
    /**
     * The constant INTENT_DATA_WRITE_CMD.
     */
    public static final int INTENT_DATA_WRITE_CMD = 13;
    /**
     * The constant INTENT_DATA_RESULT_READ_CMD.
     */
    public static final int INTENT_DATA_RESULT_READ_CMD = 14;
    /**
     * The constant INTENT_DATA_RESULT_WRITE_CMD.
     */
    public static final int INTENT_DATA_RESULT_WRITE_CMD = 15;


    /**
     * The constant INTENT_DEVICE_STATE.
     */
    public static final int INTENT_DEVICE_STATE = 67;
    /**
     * The constant INTENT_RESULT_DEVICE_STATE.
     */
    public static final int INTENT_RESULT_DEVICE_STATE = 68;
    /**
     * The constant INTENT_SEND_ERROR.
     */
    public static final int INTENT_SEND_ERROR = 69;
    /**
     * The constant INTENT_SHOW_TOAST.
     */
    public static final int INTENT_SHOW_TOAST = 70;

    /**
     * The constant ACTION_INTENT_CMD.
     */
    public final static String ACTION_INTENT_CMD = "action.intent.cmd";
    /**
     * The constant ACTION_INTENT_TOAST.
     */
    public final static String ACTION_INTENT_TOAST = "action.intent.toast";
    /**
     * The constant ACTION_INTENT_DATA.
     */
    public final static String ACTION_INTENT_DATA = "action.intent.value";
}
