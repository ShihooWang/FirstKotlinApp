package cc.bodyplus.health.ble.utils.monitor;

import java.util.LinkedList;

/**
 * Created by Shihu.Wang on 2017/11/29.
 * Email shihu.wang@bodyplus.cc
 *
 * 用来缓存心电数据的类
 */

public class LimitEcgQueue<T> {

    private int length; // 缓冲区长度

    private LinkedList<T> queue = new LinkedList<T>();

    public LimitEcgQueue(int length){
        this.length = length;
    }

    public void append(T t){
        if (queue.size() >= length){
            queue.poll();
        }
        queue.add(t);
    }

    public int getQueueSize(){
        return queue.size();
    }

//    public T poll(){
//        if (!queue.isEmpty()){
//            return queue.poll();
//        }
//        return null;
//    }

    public T get(int i){
        if (queue.size()<i+1){
            return null;
        }else {
            return queue.get(i);
        }
    }

    public void clear(){
        queue.clear();
    }

}
