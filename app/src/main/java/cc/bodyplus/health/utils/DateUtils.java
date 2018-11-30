package cc.bodyplus.health.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ameng on 15/9/22.
 */
public class DateUtils {

    public static String getHourAndMin(long stamp) {
        Date currentTime = new Date(stamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 16);
        return hour;
    }

    public static String getStringDateHourAndMin(long stamp) {
        Date currentTime = new Date(stamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(0, 16);
        return hour;
    }

    public static String getMonthFirstDay(long stamp) {
        Date now = new Date(stamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time.substring(0, 7)+"-01";
    }

    public static String getDay(String date) {
        long lcc_time = Long.valueOf(date);
        Date now = new Date(lcc_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time;
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    public static Date getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * <p/>
     * format : 格式 "yyyy-MM-dd"
     *
     * @return返回字符串格式
     */
    public static String getStringDate(String format) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 格式时间
     * <p/>
     * format : 格式 "yyyy-MM-dd"
     *
     * @return返回字符串格式
     */
    public static String getStringDate(Long time, String format) {
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);

        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss  *   * @param dateDate  * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     *
     * @return
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     *
     * @param day
     * @return
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     */
    public static String getHourAndMin() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 16);
        return hour;
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到现在分钟
     *
     * @return
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    /**
     * 返回美国时间格式 26 Apr 2006
     *
     * @param str
     * @return
     */
    public static String getEDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat
     * @return
     */
    public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }


    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    //比较两个时间大小 如果firstTime比secondTime 小，则返回true
    public static boolean compare(String firstTime, String secondTime, String format) {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了  
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //将字符串形式的时间转化为Date类型的时间  
        Date a = null;
        Date b = null;
        try {
            a = sdf.parse(firstTime);
            b = sdf.parse(secondTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Date类的一个方法，如果a早于b返回true，否则返回false  
        if (a.before(b))
            return true;
        else
            return false;  
        /* 
         * 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒 
        if(a.getTime()-b.getTime()<0) 
            return true; 
        else 
            return false; 
        */
    }

    /**
     * 得到Core log日志上传的文件名日期
     *
     * @return 字符串 yyMMdd  例如161206
     */
    public static String getStringLogDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);
        return dateString.substring(2, dateString.length());
    }


    public String formatDuring(long time) {

        long hour = time / 3600; //小时
        long minute = time % 3600 / 60; //分钟
        long second = time % 60; //秒

        if (hour > 0) {
            return hour + "," + minute + "," + second ;
        }
        if (minute > 0) {
            return  minute + "," +  second ;
        }
        return  second+"";
    }


    //通过生日得到当前年龄
    public static int userBirthdayGetAge(String birthday) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String mDateTime = formatter.format(cal.getTime());// 当前时间
        Date today = formatter.parse(mDateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birday = sdf.parse(birthday);// 当前对当前的情况
        int age = today.getYear() - birday.getYear();
        if (today.getMonth() == birday.getMonth()
                && today.getDate() == birday.getDate()
                && birday.getYear() % 4 != 0 && today.getYear() != 0
                && birday.getMonth() != 1 && today.getMonth() != 1) {// 月份和日期都与当前时间相同(除去生日和当前年是闰年，并且是二月的情况)
            return age;
        } else if (today.getMonth() < birday.getMonth()) {// 生日的月份大于当前时间的月份
            return age - 1;
        } else if (birday.getMonth() == 1 && birday.getDate() == 29// 生日是闰年，当前年不一定是闰年
                && today.getMonth() == 1) {// 生日是闰年的情况,并且本月是二月
            if (today.getYear() % 4 == 0) {// 当前年是闰年 2月有二十九
                if (today.getDate() < birday.getDate()) {//
                    return age - 1;
                } else {
                    return age;
                }
            } else {// 当前是年二月是二十八天
                if (today.getDate() < birday.getDate() - 1) {
                    return age - 1;
                } else {
                    return age;
                }
            }
        } else if (today.getMonth() == 1 && today.getDate() == 29
                && birday.getMonth() == 1) { // 当前年是闰年，生日年不一定是闰年
            if (birday.getYear() % 4 == 0) {// 生日年是闰年 二月有二十九天
                if (today.getDate() < birday.getDate()) {//
                    return age - 1;
                } else {
                    return age;
                }
            } else {// 生日年二月是二十八天
                if (today.getDate() + 1 < birday.getDate()) {
                    return age - 1;
                } else {
                    return age;
                }
            }

        } else if (today.getMonth() > birday.getMonth()) {// 不是同一年，生日的月份不大于当前的月份的情况
            return age;
        } else if (today.getDate() < birday.getDate()) {// 正常的日期(非闰年) 当前 小于 出生
            return age - 1;
        } else if (today.getDate() == birday.getDate()) {// 当前 等于 出生
            return age;
        } else {
            return age; // 当前 大于 出生
        }
    }


    //时间计数器，最多只能到99小时，如需要更大小时数需要改改方法
    public static String showTimeCount(long time) {
        if (time >= 360000000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600000) / (60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());

        long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());
        timeCount = hour + ":" + minue + ":" + sec;
        return timeCount;
    }

    public static int getBirTh(String birth){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        String nowDate = dateFormat.format( now );

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = df.parse(nowDate);
            Date d2 = df.parse(birth);

            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            System.out.println(""+days+"天");

            return (int)days/365;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String generateTimeStr(int totalTime){
        int h = totalTime/3600;
        int m = (totalTime-h*3600)/60;
        int s = totalTime%60;
        String hh = h < 10 ? "0" + h : h + "";
        String mm = m < 10 ? "0" + m : m + "";
        String ss = s < 10 ? "0" + s : s + "";
        return hh + ":" + mm + ":" + ss;
    }
}
