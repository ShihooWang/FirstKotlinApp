package cc.bodyplus;

/**
 * Created by Shihu.Wang on 2018/5/7.
 * Email shihu.wang@bodyplus.cc
 */

public class NdkJniUtils {
    static {
        System.loadLibrary("JniBodyPlus");
        System.loadLibrary("JniBodyPlusEcg120");
    }


    // ------------------------ 心率，心电 ----------------------
    /**
     * 调用JniBodyPlus 前需要初始化一次ecgProcessInit
     */
    public static native void ecgProcessInit();

    /**
     *  jintArray indata ,jintArray outdata,jint hrdata,jint arr )
     *
     * @param srcArray 输入值 每秒心电数组原始值 长度750
     * @param outArray 输出值 长度250
     * @param heart 输出值 心率值 长度为2 取第一个值
     * @param status 输出值 心率失常 长度为2 取第一个值
     * @return
     */
    public static native int ecgProcess(int[] srcArray, int[] outArray, int[] heart, int[] status);

    /**
     *
     * 初始化函数，复位心电算法
     */
    public static native void ecgAlgorithmInit();

    // ----------------------  呼吸率 -----------------
    /**
     * 初始化函数，计算呼吸率之前（每次连接上）
     */
    public static native void respProcessInit();

    /**
     *  计算呼吸率函数
     * @param inputArray 输入原始数据  长度25
     * @param output 输出值 长度2 取第一个值
     */
    public static native void respProcess(int[] inputArray,int [] output);


    //------------------------- 睡眠加速度 ----------------------
    /**
     * 初始化函数，睡眠处理函数（每次连接上）
     */
    public static native void sleepProcessInit();

    /**
     * 睡眠计算函数
     * @param inputArray 一分钟加速度数据 60*25 = 1500
     * @param output 长度2 取第一个值
     */
    public static native void sleepProcess(int[] inputArray,int [] output);



    //------------------------- 心电诊断 ----------------------
    /**
     * 60秒 心电数据处理
     * @param srcArray 输入1个通道的采样数据   长度60s X 750   45000个数据
     * @param outData  输出1个导联波形数据     长度60s X 250  15000个数据
     * @param hrData  输出心率值，无效值-100   长度2  有效为第一位
     * @param arr 输出心律失常类型        int数组  数组长度为20
     * @return
     */
    public static native int ecgOffLineProcess(int[] srcArray,int[] outData,int[] hrData,int[] arr);


    /**
     *   //  2018.2.9  睡眠的心率和呼吸波形的二次拟合算法
     JNIEXPORT jint  JNICALL Java_cc_bodyplus_NdkJniUtils_ResultStableProcess
     (JNIEnv *env, jclass cls, jintArray indata ,jintArray outdata)
     */

    public static native int ResultStableProcess(int[] srcArray,int[] outData);

}
