package com.telephone.coursetable.Clock;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.ShowTableNode;
import com.telephone.coursetable.Database.TermInfo;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Database.UserDao;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Clock {

    public static final long NO_TERM = 0;

    /**
     * @param sts the timestamp of start
     * @param nts the timestamp of current
     * @return
     * - 0 : sts > nts
     * - other : week num of specified start time and specified current time
     * @clear
     */
    public static long whichWeek(final long sts, final long nts){
        long res = 0;
        if (nts >= sts){
            res = 1;
            res += (nts - sts) / 604800000L;
        }
        return res;
    }

    /**
     *
     * @param sts
     * @param week must > 0
     * @param weekday must > 0
     * @return 0 if wrong, else timestamp
     */
    public static long getTimeStampForWeekAndWeekdaySince(long sts, long week, long weekday){
        if (week <= 0 || weekday <= 0){
            return 0;
        }
        week--;
        weekday--;
        return ( sts + (week * 604800000L) + (weekday * 86400000L) + 3600000L );
    }

    /**
     * the Timezone used to convert the string in the SharedPreferences to {@link Date} is represented by the formatter passed in
     * @param nts the timestamp of current
     * @param pref the SharedPreferences storing time period information
     * @param timenos the array of time code
     * @param formatter the SimpleDateFormat of the time period information in the SharedPreferences
     * @param s_suffix the suffix of start time key
     * @param e_suffix the suffix of end time key
     * @param d_suffix the suffix of description key
     * @return
     * - null : corresponding time period not found
     * - not null : corresponding time period of specified current time
     * @clear
     */
    public static TimeAndDescription whichTime_low_api(long nts, SharedPreferences pref, String[] timenos, SimpleDateFormat formatter, String s_suffix, String e_suffix, String d_suffix){
        nts %= ( 1000L * 60L * 60L * 24L );
        TimeAndDescription res = null;
        Date n = new Date(nts);
        for (String timeno : timenos) {
            String sj = pref.getString(timeno + s_suffix, null);
            String ej = pref.getString(timeno + e_suffix, null);
            String des = pref.getString(timeno + d_suffix, null);
            if (sj != null && ej != null && des != null) {
                try {
                    Date sl = formatter.parse(sj);
                    Date el = formatter.parse(ej);
                    if (sl.before(n) || sl.equals(n)){
                        if (el.after(n) || el.equals(n)){
                            res = new TimeAndDescription(timeno, des);
                            return res;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return res;
                }
            }
        }
        return res;
    }

    /**
     * find a start time with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static Date getStartTimeUsingDefaultConfig_low_api(Context c, String time_id){
        String key = time_id + getSSFor_locateNow(c);
        String time_str = MyApp.getCurrentSharedPreference().getString(key, null);
        if (time_str == null){
            return null;
        }else {
            try {
                return getDateTimeFormatterFor_locateNow_low_api(c).parse(time_str);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * find an end time with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static Date getEndTimeUsingDefaultConfig_low_api(Context c, String time_id){
        String key = time_id + getESFor_locateNow(c);
        String time_str = MyApp.getCurrentSharedPreference().getString(key, null);
        if (time_str == null){
            return null;
        }else {
            try {
                return getDateTimeFormatterFor_locateNow_low_api(c).parse(time_str);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * find a time description with specified time id using default config.
     * if not found, return null.
     * @clear
     */
    public static String getTimeDesUsingDefaultConfig(Context c, String time_id){
        String key = time_id + getDSFor_locateNow(c);
        return MyApp.getCurrentSharedPreference().getString(key, null);
    }

    /**
     * @param nts the timestamp of current
     * @param tdao the {@link com.telephone.coursetable.Database.TermInfoDao} used to query the database for term info
     * @param pref {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @param times {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @param pref_time_period_formatter {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @param pref_s_suffix {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @param pref_e_suffix {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @param pref_d_suffix {@link #whichTime_low_api(long, SharedPreferences, String[], SimpleDateFormat, String, String, String)}
     * @return a {@link Locate}
     *      - res.{@link Locate#term} : corresponding {@link TermInfo} of specified current time, null if not found
     *      - res.{@link Locate#week} : corresponding week num of specified current time, {@link #NO_TERM} if res.{@link Locate#term} is null
     *      - res.{@link Locate#weekday} : weekday of specified current time
     *      - res.{@link Locate#month} : month of specified current time
     *      - res.{@link Locate#day} : day of specified current time
     *      - res.{@link Locate#time} : corresponding time code of specified current time, null if not found
     *      - res.{@link Locate#time_description} : corresponding time description of specified current time, null if not found
     * @clear
     */
    public static Locate locateNow_low_api(long nts, TermInfoDao tdao, SharedPreferences pref, String[] times, SimpleDateFormat pref_time_period_formatter, String pref_s_suffix, String pref_e_suffix, String pref_d_suffix){
        Locate res = new Locate(null, NO_TERM, 0, 0, 0, null, null);
        Date n = new Date(nts);
        List<TermInfo> which_term_res = tdao.whichTerm(nts);
        if (!which_term_res.isEmpty()){
            res.term = which_term_res.get(0);
            res.week = whichWeek(res.term.sts, nts);
        }
        // use default Timezone to calculate the month/day/weekday
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(n);
        res.weekday = ( (calendar.get(Calendar.DAY_OF_WEEK) == 1) ? (7) : (calendar.get(Calendar.DAY_OF_WEEK) - 1) );
        res.month = calendar.get(Calendar.MONTH) + 1;
        res.day = calendar.get(Calendar.DAY_OF_MONTH);
        TimeAndDescription which_time_res = whichTime_low_api(nts, pref, times, pref_time_period_formatter, pref_s_suffix, pref_e_suffix, pref_d_suffix);
        if (which_time_res != null){
            res.time = which_time_res.time;
            res.time_description = which_time_res.des;
        }
        return res;
    }

    /**
     * @clear
     */
    public static SimpleDateFormat getDateTimeFormatterFor_locateNow_low_api(Context c){
        // 默认使用 Locale.US
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(c.getResources().getString(R.string.server_hours_time_format), Locale.US);
        // 文件中记录的是北京时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return simpleDateFormat;
    }

    /**
     * @clear
     */
    public static String getSSFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_start_suffix);
    }

    /**
     * @clear
     */
    public static String getESFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_end_suffix);
    }

    /**
     * @clear
     */
    public static String getDSFor_locateNow(Context c){
        return c.getResources().getString(R.string.pref_hour_des_suffix);
    }

    /**
     * @return now time stamp
     * @clear
     */
    public static long nowTimeStamp(){
        return System.currentTimeMillis();
    }

    /**
     * the Timezone used to convert the string in the SharedPreferences to {@link Date} is represented by the formatter passed in
     * @return
     * null means can not get time from file
     */
    public static Map.Entry<Integer, Integer> findNowTime_low_api(long nts, SharedPreferences pref, SimpleDateFormat formatter, String s_suffix, String e_suffix) {
        nts %= ( 1000L * 60L * 60L * 24L );
        Map.Entry<Integer, Integer> res = null;
        try {
            Date start = formatter.parse("00:00");
            Date end = formatter.parse("23:59");
            Date n = new Date(nts);
            List<Integer> timelist = new ArrayList<>();

            timelist.add(-1);
            for (int i = 0; i < MyApp.times.length; i++) {
                timelist.add(i);
                timelist.add(i);
            }
            timelist.add(-1);

            for (int i = 0; i < timelist.size() - 1; i++) {
                if (!timelist.get(i).equals(-1) && !timelist.get(i + 1).equals(-1)) {
                    String sj;
                    String ej;
                    if (timelist.get(i).equals(timelist.get(i + 1))) {
                        sj = pref.getString(MyApp.times[timelist.get(i)] + s_suffix, null);
                        ej = pref.getString(MyApp.times[timelist.get(i + 1)] + e_suffix, null);
                    } else {
                        sj = pref.getString(MyApp.times[timelist.get(i)] + e_suffix, null);
                        ej = pref.getString(MyApp.times[timelist.get(i + 1)] + s_suffix, null);
                    }
                    if (sj != null && ej != null) {
                        Date sl = formatter.parse(sj);
                        Date el = formatter.parse(ej);
                        if (timelist.get(i).equals(timelist.get(i + 1))) {
                            if (sl.before(n) || sl.equals(n)) {
                                if (el.after(n) || el.equals(n)) {
                                    res = Map.entry(timelist.get(i), timelist.get(i + 1));
                                    break;
                                }
                            }
                        } else {
                            if (sl.before(n)) {
                                if (el.after(n)) {
                                    res = Map.entry(timelist.get(i), timelist.get(i + 1));
                                    break;
                                }
                            }
                        }
                    }
                } else if (timelist.get(i).equals(-1)) {
                    Date st = formatter.parse(pref.getString(MyApp.times[timelist.get(i + 1)] + s_suffix, null));
                    if (start.before(n) || start.equals(n)) {
                        if (st != null && st.after(n)) {
                            res = Map.entry(timelist.get(i), timelist.get(i + 1));
                            break;
                        }
                    }
                } else if (timelist.get(i + 1).equals(-1)) {
                    Date et = formatter.parse(pref.getString(MyApp.times[timelist.get(i)] + e_suffix, null));
                    if (et != null && et.before(n)) {
                        if (end.after(n) || end.equals(n)) {
                            res = Map.entry(timelist.get(i), timelist.get(i + 1));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    /**
     * @return
     * null 找不到当前时间段 || 处于假期 || 当天没课
     */
    public static FindClassOfCurrentOrNextTimeRes findClassOfCurrentOrNextTime_low_api(String username, long nts, TermInfoDao tdao, SharedPreferences pref, SimpleDateFormat formatter, String s_suffix, String e_suffix, String d_suffix) {
        Map.Entry<Integer, Integer> nowTime = findNowTime_low_api(nts, pref, formatter, s_suffix, e_suffix);
        List<ShowTableNode> surplus = new LinkedList<>();
        Locate locateNow = locateNow_low_api(nts, tdao, pref, MyApp.times, formatter, s_suffix, e_suffix, d_suffix);
        TermInfo term = locateNow.term;
        long week = locateNow.week;
        long weekday = locateNow.weekday;
        if ( nowTime == null ) return null;
        if ( term == null ) return null;
        if ( MyApp.getCurrentAppDB().goToClassDao().getTodayLessons(username, term.term, week, weekday).isEmpty() ) return null;
        int time = nowTime.getValue();
        if ( time == -1 ) return new FindClassOfCurrentOrNextTimeRes(surplus);
        do {
            surplus = MyApp.getCurrentAppDB().goToClassDao().getNode(username, term.term, week, weekday, MyApp.times[time] );
        }while ( surplus.isEmpty() && (++time) < MyApp.times.length );
        if (nowTime.getKey().equals(time)){
            return new FindClassOfCurrentOrNextTimeRes(surplus, true);
        }else {
            String start = null;
            if (time < MyApp.times.length) {
                start = pref.getString(MyApp.times[time] + s_suffix, null);
            }
            return new FindClassOfCurrentOrNextTimeRes(surplus, false, start);
        }
    }

    public static String comment_past_time(long compare_timestamp){

        Date now = new Date(compare_timestamp);

        long NowTime = nowTimeStamp();
        long gap = NowTime - compare_timestamp;
        String tip = "";

        if(gap < 0L){
            tip = "未来";
        }

        //1分钟以内
        long gap_temp = gap/60000L;
        if(gap_temp == 0L){
            tip = "刚刚";
        }

        //1分钟到60分钟之间
        if(1L <= gap_temp && gap_temp < 60L){
            tip = gap_temp + "分钟前";
        }

        //1个小时到1天之间
        gap_temp /= 60L;
        if(1L <= gap_temp && gap_temp < 24L){
            tip = gap_temp + "小时前";
        }

        //1天到1个月之间
        gap_temp /= 24L;
        if(1L <= gap_temp && gap_temp < 30L){
            tip = gap_temp + "天前";
        }

        //1个月到1年之间
        gap_temp /= 30L;
        if(1L <= gap_temp && gap_temp < 12L){
            tip = gap_temp + "个月前";
        }

        //大于1年前
        gap_temp /= 12L;
        if(1 <= gap_temp){
            tip = gap_temp + "年前";
        }

        return tip;
    }

}
