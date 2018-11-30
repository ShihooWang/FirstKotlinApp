package cc.bodyplus.health.ble.parse;

/**
 * Created by Shihu.Wang on 2017/12/12.
 * Email shihu.wang@bodyplus.cc
 */

public class M2MegicType {
    public static int ECG_DATA_LENGTH = 500;
    public static int BREATH_DATA_LENGTH = 50;
    public static int ACC_DATA_LENGTH = 150;

    /**
     *  简化帧	        0001	0x1EDF	1001	0x9EDF
        简化帧(含扩展)	0101	0x5EDF	1101	0xDEDF
        扩展帧	        0100	0x4EDF	1100	0xCEDF
        标准帧	        0011	0x3EDF	1011	0xBEDF
        标准帧(含扩展)	0111	0x7EDF	1111	0xFEDF
     */

    /**
     * 通用逻辑头第一个字节
     */
    public static final byte MAGIC_FIRST_BYTE = (byte)0xDF;

    /**
     * 简化帧无应答第二个字节
     */
    public static final byte MAGIC_SIMPLE_NO_RE_BYTE = (byte)0x1E;

    /**
     * 简化帧有应答第二个字节
     */
    public static final byte MAGIC_SIMPLE_RES_BYTE = (byte)0x9E;

    /**
     * 简化帧(含扩展)无应答第二个字节
     */
    public static final byte MAGIC_SIMPLE_EXTEND_NO_RES_BYTE = (byte)0x5E;

    /**
     * 简化帧(含扩展)有应答第二个字节
     */
    public static final byte MAGIC_SIMPLE_EXTEND_AND_RES_BYTE = (byte)0xDE;

    /**
     * 扩展帧无应答第二个字节
     */
    public static final byte MAGIC_EXTEND_NO_RES_BYTE = (byte)0x4E;

    /**
     * 扩展帧有应答第二个字节
     */
    public static final byte MAGIC_EXTEND_AND_RES_BYTE = (byte)0xCE;

    /**
     * 标准帧无应答第二个字节
     */
    public static final byte MAGIC_STANDARD_NO_RES_BYTE = (byte)0x3E;

    /**
     * 标准帧有应答第二个字节
     */
    public static final byte MAGIC_STANDARD_AND_RES_BYTE = (byte)0xBE;

    /**
     * 标准帧(含扩展)无应答第二个字节
     */
    public static final byte MAGIC_STANDARD_EXTEND_NO_RES_BYTE = (byte)0x7E;

    /**
     *  标准帧(含扩展)有应答第二个字节
     */
    public static final byte MAGIC_STANDARD_EXTEND_AND_RES_BYTE = (byte)0xFE;


    /**
     *  工厂测试	    睡眠	    CORE_TYPE_FACT_TEST	    0x2E	975	    标准帧	    N	    未出厂，没过终测时的模式
         个人健身	运动	    CORE_TYPE_SPORT_OLD	    0x51	11	    OLD	        Y	    S02原有帧格式
         个人健身	运动	    CORE_TYPE_SPORT_NEW	    0x41	5	    简化/扩展帧	Y	    S02精简数据
         团队健身	运动	    CORE_TYPE_TEAM_NOLK	    0xA1	5	    简化/扩展帧	Y	    S02无连接
         医疗监测	呼吸	    CORE_TYPE_MEDC_RESP	    0x0E	975	    标准帧	    N	    S06所有波形数据
         医疗监测	心电	    CORE_TYPE_MEDC_ECG	    0x0A	900	    标准帧	    N	    S06 心电-实时 模式
         医疗监测	存储	    CORE_TYPE_MEDC_STOR	    0x82	900	    标准帧	    N	    S06 心电-存储 模式
         医疗监测	心率	    CORE_TYPE_MEDC_HRRT	    0x81	900	    简化帧	    N	    S06 心率 模式
         医疗监测	睡眠	    CORE_TYPE_MEDC_SLEP	    0x91	900	    简化帧	    N	    S06 睡眠 模式
     */

    /**
     * 工厂测试	睡眠	CORE_TYPE_FACT_TEST	0x0E 975	标准帧	N	未出厂，没过终测时的模式
     */
    public static final byte CORE_TYPE_FACT_TEST = (byte)0x0E;

    /**
     * 个人健身	运动	CORE_TYPE_SPORT_OLD	0x51 S02原有帧格式
     */
    public static final byte CORE_TYPE_SPORT_OLD = (byte)0x51;

    /**
     * 个人健身	运动	CORE_TYPE_SPORT_NEW	0x41 S02精简数据
     */
    public static final byte CORE_TYPE_SPORT_NEW = (byte)0x41;

    /**
     * 团队健身	运动	CORE_TYPE_TEAM_NOLK	0x81 S06 心率 模式
     */
    public static final byte CORE_TYPE_TEAM_NOLK = (byte)0xA1;

    /**
     * 医疗监测	呼吸	CORE_TYPE_MEDC_RESP	0x0E	975	标准帧	N	S06所有波形数据  S06所有波形数据
     */
    public static final byte CORE_TYPE_MEDC_RESP  = (byte)0x0E;

    /**
     * 医疗监测	心电	    CORE_TYPE_MEDC_ECG	    0x0A	900	    标准帧	    N	    S06 心电-实时 模式
     */
    public static final byte CORE_TYPE_MEDC_ECG  = (byte)0x0A;

    /**
     * 医疗监测	存储	    CORE_TYPE_MEDC_STOR	    0x82	750+20	    标准帧	    N	    S06 心电-存储 模式
     */
    public static final byte CORE_TYPE_MEDC_STOR  = (byte)0x82;

    /**
     * 医疗监测	心率	    CORE_TYPE_MEDC_HRRT	    0x81	5	    简化帧	    N	    S06 心率 模式
     */
    public static final byte CORE_TYPE_MEDC_HRRT  = (byte)0x81;

    /**
     * 医疗监测	睡眠	    CORE_TYPE_MEDC_SLEP	    0x91	5	    简化帧	    N	    S06 睡眠 模式
     */
//    public static final byte CORE_TYPE_MEDC_SLEP = (byte)0x91;
}
