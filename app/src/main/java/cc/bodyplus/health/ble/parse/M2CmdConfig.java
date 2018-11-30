package cc.bodyplus.health.ble.parse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Shihu.Wang on 2017/5/5.
 * Email shihu.wang@bodyplus.cc
 * <p>
 * S02蓝牙命令列表
 */
public class M2CmdConfig {

    // 数据上传总开关
    public static final short BLE_CMD_UPLOAD_DATA = (short) 12;

    public static final short BLE_CMD_UPLOAD_DATA_ASK = (short) 0x01;

    // 历史数据上传使能 W：2 DATA_UPL,3 DATA_END，4 DATA_ERS R：1 DATA_ASK，8 DATA_INF
    public static final short BLE_CMD_UPLOAD_OLDDATA = (short) 19;

    public static final short UPLOAD_OLDDATA_UPL = (short) 0x02;
    public static final short UPLOAD_OLDDATA_END = (short) 0x03;
    public static final short UPLOAD_OLDDATA_ERS = (short) 0x04;
    public static final short UPLOAD_OLDDATA_ASK = (short) 0x01;
    public static final short UPLOAD_OLDDATA_INF = (short) 0x08;
    public static final short UPLOAD_OLDDATA_TYP = (short) 0x09;
    public static final short UPLOAD_OLDDATA_NUM = (short) 0x05;
    public final static short BLE_CMD_SLEEP_THRESHOLD = (short) 88; // 浮点型，睡眠浅睡阈值
    public final static short BLE_CMD_SLEEP_THRESHOLD_N = (short) 92; // 浮点型，浅睡深睡阈值


    public final static int DATA_ASK_DATA_LENGHT = 6;
    public final static int DATA_UNM_DATA_LENGHT = 10;
    public final static int DATA_INF_DATA_LENGHT = 13;
    public final static int DATA_INF_DATA_LENGHT_S06 = 14;

    // 用户自定义名称 长度12 默认值BodyPlus  蓝牙自定义名称 取代S01版本 NAM
    public static final short BLE_CMD_USER_NAME = (short) 20;
    /**
     * The constant BLE_CMD_DFU_JUMP_LENGTH.
     */
// 升级跳转  1 跳转到升级模式 取代DFU
    public static final short BLE_CMD_DFU_JUMP_LENGTH = (short) 33;
    /**
     * The constant BLE_CMD_LED_STOP_LENGTH.
     */
// 关闭LED显示  0 关闭LED显示； 1 正常显示
    public static final short BLE_CMD_LED_STOP_LENGTH = (short) 34;
    /**
     * The constant BLE_CMD_FILT_STORE_CNT_LENGTH.
     */
// 存储数据量 1到10个 默认3个
    public static final short BLE_CMD_FILT_STORE_CNT_LENGTH = (short) 80;
    /**
     * The constant BLECMD_FILT_FISRT_JUMP_LENGTH.
     */
// 忽略心率个数 第一次开启时跳过的心率个数，不参与滤波计算  默认5个
    public static final short BLECMD_FILT_FISRT_JUMP_LENGTH = (short) 81;
    /**
     * The constant BLECMD_FILT_WAVAMP_ALL_LENGTH.
     */
// 整体波动阈值  默认5个
    public static final short BLECMD_FILT_WAVAMP_ALL_LENGTH = (short) 82;
    /**
     * The constant BLECMD_FILT_WAVAMP_LAST_LENGTH.
     */
// 当前波动阈值  默认5个
    public static final short BLECMD_FILT_WAVAMP_LAST_LENGTH = (short) 83;

    // core类型切换
    public static final short BLECMD_CORE_TYPE = (short) 45;

    /**
     * The constant mCommandPayloadLengthMap.
     */
    public static Map<Short, Byte> mCommandPayloadLengthMap = new LinkedHashMap<>();

    static {
        mCommandPayloadLengthMap.put(BLE_CMD_USER_NAME, (byte) 12);
        mCommandPayloadLengthMap.put(BLE_CMD_DFU_JUMP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLE_CMD_LED_STOP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLE_CMD_FILT_STORE_CNT_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLECMD_FILT_FISRT_JUMP_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLECMD_FILT_WAVAMP_ALL_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLECMD_FILT_WAVAMP_LAST_LENGTH, (byte) 1);
        mCommandPayloadLengthMap.put(BLECMD_CORE_TYPE, (byte) 1);
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


}
