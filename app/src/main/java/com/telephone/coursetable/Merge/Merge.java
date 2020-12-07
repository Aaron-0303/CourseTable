package com.telephone.coursetable.Merge;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.telephone.coursetable.Database.CETDao;
import com.telephone.coursetable.Database.ClassInfo;
import com.telephone.coursetable.Database.ClassInfoDao;
import com.telephone.coursetable.Database.ExamInfoDao;
import com.telephone.coursetable.Database.ExamTotal;
import com.telephone.coursetable.Database.GoToClass;
import com.telephone.coursetable.Database.GoToClassDao;
import com.telephone.coursetable.Database.GradeTotal;
import com.telephone.coursetable.Database.GradesDao;
import com.telephone.coursetable.Database.GraduationScoreDao;
import com.telephone.coursetable.Database.Key.GoToClassKey;
import com.telephone.coursetable.Database.LABDao;
import com.telephone.coursetable.Database.MyComment;
import com.telephone.coursetable.Database.PersonInfoDao;
import com.telephone.coursetable.Database.TermInfoDao;
import com.telephone.coursetable.Gson.CET;
import com.telephone.coursetable.Gson.CET_s;
import com.telephone.coursetable.Gson.ExamInfo;
import com.telephone.coursetable.Gson.ExamInfo_s;
import com.telephone.coursetable.Gson.GoToClass_ClassInfo;
import com.telephone.coursetable.Gson.GoToClass_ClassInfo_s;
import com.telephone.coursetable.Gson.Grades;
import com.telephone.coursetable.Gson.Grades_s;
import com.telephone.coursetable.Gson.GraduationScore;
import com.telephone.coursetable.Gson.GraduationScore_s;
import com.telephone.coursetable.Gson.Hour;
import com.telephone.coursetable.Gson.Hour_s;
import com.telephone.coursetable.Gson.LAB;
import com.telephone.coursetable.Gson.LAB_s;
import com.telephone.coursetable.Gson.PersonInfo;
import com.telephone.coursetable.Gson.PersonInfo_s;
import com.telephone.coursetable.Gson.StudentInfo;
import com.telephone.coursetable.Gson.TermInfo;
import com.telephone.coursetable.Gson.TermInfo_s;
import com.telephone.coursetable.MyApp;
import com.telephone.coursetable.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Merge {

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void personInfo(@NonNull String origin_p, @NonNull String origin_stu, @NonNull PersonInfoDao pdao){
        PersonInfo_s p_s = MyApp.gson.fromJson(origin_p, PersonInfo_s.class);
        PersonInfo p = p_s.getData();
        StudentInfo stu = MyApp.gson.fromJson(origin_stu, StudentInfo.class);
        pdao.insert(
                new com.telephone.coursetable.Database.PersonInfo(
                        p.getStid(),p.getGrade(),p.getClassno(),p.getSpno(),p.getName(),p.getName1(),
                        p.getEngname(),p.getSex(),p.getPass(),p.getDegree(),p.getDirection(),p.getChangetype(),
                        p.getSecspno(),p.getClasstype(),p.getIdcard(),p.getStype(),p.getXjzt(),p.getChangestate(),
                        p.getLqtype(),p.getZsjj(),p.getNation(),p.getPolitical(),p.getNativeplace(),
                        p.getBirthday(),p.getEnrolldate(),p.getLeavedate(),p.getDossiercode(),p.getHostel(),
                        p.getHostelphone(),p.getPostcode(),p.getAddress(),p.getPhoneno(),p.getFamilyheader(),
                        p.getTotal(),p.getChinese(),p.getMaths(),p.getEnglish(),p.getAddscore1(),
                        p.getAddscore2(),p.getComment(),p.getTestnum(),p.getFmxm1(),p.getFmzjlx1(),
                        p.getFmzjhm1(),p.getFmxm2(),p.getFmzjlx2(),p.getFmzjhm2(),p.getDs(),p.getXq(),
                        p.getRxfs(),p.getOldno(),stu.getDptno(), stu.getDptname(), stu.getSpname()
                )
        );
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void goToClass_ClassInfo(@NonNull String origin_g, @NonNull GoToClassDao gdao, @NonNull ClassInfoDao cdao, @NonNull HashMap<GoToClassKey, String> my_comment_map, @NonNull String username) {
        GoToClass_ClassInfo_s g_s = MyApp.gson.fromJson(origin_g, GoToClass_ClassInfo_s.class);
        List<GoToClass_ClassInfo> g = g_s.getData();
        for (GoToClass_ClassInfo i : g) {
            gdao.insert(
                    new GoToClass(
                            username,
                            i.getTerm(), i.getWeek(), i.getSeq(), i.getCourseno(), i.getStartweek(),
                            i.getEndweek(), i.isOddweek(), i.getId(), i.getCroomno(), i.getHours(),
                            i.getComm(),
//                            my_comment_map.get(new GoToClassKey(
//                                    username,
//                                    i.getTerm(), i.getWeek(), i.getSeq(), i.getCourseno(), i.getStartweek(),
//                                    i.getEndweek(), i.isOddweek()
//                            )),
                            MyApp.getCurrentAppDB().myCommentDao().getComment(MyApp.gson.toJson(
                                    new GoToClassKey(
                                            username,
                                            i.getTerm(), i.getWeek(), i.getSeq(), i.getCourseno(), i.getStartweek(),
                                            i.getEndweek(), i.isOddweek()
                                    )
                            )),
                            false
                    )
            );
            cdao.insert(
                    new ClassInfo(
                            username,
                            i.getCourseno(), i.getCtype(), i.getTname(), i.getExamt(), i.getDptname(),
                            i.getDptno(), i.getSpname(), i.getSpno(), i.getGrade(), i.getCname(),
                            i.getTeacherno(), i.getName(), i.getCourseid(), i.getMaxcnt(),
                            i.getXf(), i.getLlxs(), i.getSyxs(), i.getSjxs(), i.getQtxs(), i.getSctcnt(), 0
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void graduationScore(@NonNull String origin_g, @NonNull String origin_g2, @NonNull GraduationScoreDao gdao){
        GraduationScore_s yxxf = MyApp.gson.fromJson(origin_g, GraduationScore_s.class);
        GraduationScore_s plan_cj = MyApp.gson.fromJson(origin_g2, GraduationScore_s.class);
        List<GraduationScore> yxxf_list = yxxf.getData();
        List<GraduationScore> plan_cj_list = plan_cj.getData();
        //plan cj
        for (GraduationScore cj : plan_cj_list){
            gdao.insert(
                    new com.telephone.coursetable.Database.GraduationScore(
                            cj.getName(), cj.getCname(), cj.getEngname(), cj.getEngcj(), cj.getTname(),
                            cj.getStid(), cj.getTerm(), cj.getCourseid(), cj.getPlanxf(), cj.getCredithour(),
                            cj.getCoursetype(), cj.getLvl(), cj.getSterm(), cj.getCourseno(), cj.getScid(),
                            cj.getScname(), cj.getScore(), cj.getZpxs(), cj.getXf(), cj.getStp()
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void termInfo(@NonNull Context c, @NonNull String origin_t, @NonNull TermInfoDao tdao){
        TermInfo_s t_s = MyApp.gson.fromJson(origin_t, TermInfo_s.class);
        List<TermInfo> t = t_s.getData();
        Resources r = c.getResources();
        SimpleDateFormat server_formatter = new SimpleDateFormat(r.getString(R.string.server_terminfo_datetime_format), Locale.US);
        server_formatter.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        for (TermInfo i : t){
            long sts = 1;
            long ets = 2;
            try {
                sts = server_formatter.parse(i.getStartdate()).getTime();
                ets = server_formatter.parse(i.getEnddate()).getTime();
            }catch (ParseException e) {
                e.printStackTrace();
            }
            tdao.insert(
                    new com.telephone.coursetable.Database.TermInfo(
                            i.getTerm(), i.getStartdate(), i.getEnddate(), i.getWeeknum(), i.getTermname(),
                            i.getSchoolyear(), i.getComm(), sts, ets
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void hour(@NonNull Context c, @NonNull String origin_h, @NonNull SharedPreferences.Editor editor){
        Hour_s h_s = MyApp.gson.fromJson(origin_h, Hour_s.class);
        List<Hour> h = h_s.getData();
        Resources r = c.getResources();
        String ss = r.getString(R.string.pref_hour_start_suffix);
        String sbs = r.getString(R.string.pref_hour_start_backup_suffix);
        String es = r.getString(R.string.pref_hour_end_suffix);
        String ebs = r.getString(R.string.pref_hour_end_backup_suffix);
        String ds = r.getString(R.string.pref_hour_des_suffix);
        String dbs = r.getString(R.string.pref_hour_des_backup_suffix);
        // edit by Telephone 2020/11/25, use predefined time
//        for (Hour i : h){
//            String memo = i.getMemo();
//            if (memo == null || memo.isEmpty()){
//                continue;
//            }
//            String des = i.getNodename();
//            String node_no = i.getNodeno();
//            int index = memo.indexOf('-');
//            String stime = memo.substring(0, index);
//            String etime = memo.substring(index + 1);
//            editor.putString(node_no + ss, stime);
//            editor.putString(node_no + es, etime);
//            editor.putString(node_no + ds, des);
//            editor.putString(node_no + sbs, stime);
//            editor.putString(node_no + ebs, etime);
//            editor.putString(node_no + dbs, des);
//        }
        editor.putString("1" + ss, "8:25");
        editor.putString("1" + sbs, "8:25");
        editor.putString("1" + es, "10:00");
        editor.putString("1" + ebs, "10:00");
        editor.putString("1" + ds, "第一大节");
        editor.putString("1" + dbs, "第一大节");
        editor.putString("2" + ss, "10:25");
        editor.putString("2" + sbs, "10:25");
        editor.putString("2" + es, "12:00");
        editor.putString("2" + ebs, "12:00");
        editor.putString("2" + ds, "第二大节");
        editor.putString("2" + dbs, "第二大节");
        editor.putString("3" + ss, "14:30");
        editor.putString("3" + sbs, "14:30");
        editor.putString("3" + es, "16:05");
        editor.putString("3" + ebs, "16:05");
        editor.putString("3" + ds, "第三大节");
        editor.putString("3" + dbs, "第三大节");
        editor.putString("4" + ss, "16:30");
        editor.putString("4" + sbs, "16:30");
        editor.putString("4" + es, "18:05");
        editor.putString("4" + ebs, "18:05");
        editor.putString("4" + ds, "第四大节");
        editor.putString("4" + dbs, "第四大节");
        editor.putString("5" + ss, "19:00");
        editor.putString("5" + sbs, "19:00");
        editor.putString("5" + es, "20:30");
        editor.putString("5" + ebs, "20:30");
        editor.putString("5" + ds, "第五大节");
        editor.putString("5" + dbs, "第五大节");
        editor.commit();
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void grades(@NonNull String origin_g, @NonNull GradesDao gdao, boolean formal, boolean test){
        if (formal){
            MyApp.getDb_compare().gradeTotalDao().insert(new GradeTotal(origin_g));
        }
        if (test){
            MyApp.getDb_compare_test().gradeTotalDao().insert(new GradeTotal(origin_g));
        }
        Grades_s g_s = MyApp.gson.fromJson(origin_g, Grades_s.class);
        List<Grades> g = g_s.getData();
        for (Grades i : g){
            gdao.insert(
                    new com.telephone.coursetable.Database.Grades(
                            i.getDptno(), i.getDptname(), i.getSpno(), i.getSpname(), i.getBj(), i.getGrade(),
                            i.getStid(), i.getName(), i.getTerm(), i.getCourseid(), i.getCourseno(),
                            i.getCname(), i.getCourselevel(), i.getScore(), i.getZpxs(), i.getKctype(),
                            i.getTypeno(), i.getCid(), i.getCno(), i.getSycj(), i.getQzcj(), i.getPscj(),
                            i.getKhcj(), i.getZpcj(), i.getKslb(), i.getCjlb(), i.getKssj(), i.getXf(),
                            i.getXslb(), i.getTname1(), i.getStage(), i.getExamt(), i.getXs(), i.getCjlx(),
                            i.getChk(), i.getComm()
                    )
            );
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void examInfo(@NonNull String origin_e, @NonNull ExamInfoDao edao, @NonNull TermInfoDao termInfoDao, @NonNull Context c, boolean formal, boolean test, @NonNull String sid){
        if (formal){
            MyApp.getDb_compare().examTotalDao().insert(new ExamTotal(origin_e));
        }
        if (test){
            MyApp.getDb_compare_test().examTotalDao().insert(new ExamTotal(origin_e));
        }
        ExamInfo_s e_s = MyApp.gson.fromJson(origin_e, ExamInfo_s.class);
        List<ExamInfo> e = e_s.getData();
        for (ExamInfo i : e){
            if (i.getKssj() == null){
                i.setKssj("");
            }
            if (i.getExamdate() == null){
                i.setExamdate("");
            }
            edao.insert(new com.telephone.coursetable.Database.ExamInfo(
                    no_null_string(i.getCroomno()), i.getCroomname(), i.getTch(), i.getTch1(), i.getTch2(), i.getJs(),
                    i.getJs1(), i.getJs2(), i.getRoomrs(), i.getTerm(), i.getGrade(), i.getDpt(),
                    i.getSpno(), i.getSpname(), i.getCourseid(),no_null_string(i.getCourseno()), i.getLabno(), i.getLabname(),
                    i.getDptno(), i.getTeacherno(), i.getName(), i.getXf(), i.getCname(), i.getSctcnt(),
                    i.getStucnt(), i.getScoretype(), i.getExamt(), i.getKctype(), i.getTypeno(),
                    i.getExamdate(), i.getExamtime(), i.getExamstate(), i.getExammode(), i.getXm(),
                    i.getRefertime(), i.getZc(), i.getXq(), i.getKsjc(), i.getJsjc(), i.getBkzt(),
                    i.getKssj(), no_null_string(i.getComm()), i.getRooms(), i.getLsh(), i.getZone(), i.getChecked1(),
                    i.getPostdate(), i.getOperator(), termInfoDao, c, sid
            ));
        }
    }
    private static String no_null_string(String s){return (s == null)?(""):(s);}

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void cet(@NonNull String origin_cet, @NonNull CETDao cetDao){
        CET_s c_s = MyApp.gson.fromJson(origin_cet, CET_s.class);
        List<CET> c = c_s.getData();
        for (CET i : c){
            cetDao.insert(new com.telephone.coursetable.Database.CET(
                    i.getName(), i.getSex(), i.getPostdate(), i.getDptno(), i.getDptname(), i.getSpno(),
                    i.getSpname(), i.getGrade(), i.getBj(), i.getTerm(), i.getStid(), i.getCode(),
                    i.getScore(), i.getStage(), i.getCard(), i.getOperator()
            ));
        }
    }

    /**
     * the origin must have corresponding content
     * @clear
     */
    public static void lab(@NonNull String origin_lab, @NonNull LABDao labDao, @NonNull GoToClassDao goToClassDao, @NonNull ClassInfoDao classInfoDao, @NonNull HashMap<GoToClassKey, String> my_comment_map, @NonNull String username){
        LAB_s lab_s = MyApp.gson.fromJson(origin_lab, LAB_s.class);
        List<LAB> labList = lab_s.getData();
        for (LAB lab : labList) {
            String time;
            if (lab.getJc() < 1){
                time = "1";
            }else if (lab.getJc() > MyApp.times.length){
                time = MyApp.times[MyApp.times.length - 1];
            }else {
                time = lab.getJc()+"";
            }
            long jc_long = Long.parseLong(time);
            labDao.insert(new com.telephone.coursetable.Database.LAB(
                    lab.getTerm(), lab.getLabid(), lab.getItemname(), lab.getCourseid(), lab.getCname(),
                    lab.getSpno(), lab.getSpname(), lab.getGrade(), lab.getTeacherno(), lab.getName(),
                    lab.getSrname(), lab.getSrdd(), lab.getXh(), lab.getBno(), lab.getPersons(),
                    lab.getZc(), lab.getXq(), jc_long, lab.getJc1(), lab.getAssistantno(), lab.getTeachers(),
                    lab.getComm(), lab.getCourseno(), lab.getStusct(), lab.getSrid()
            ));
            goToClassDao.insert(new GoToClass(
                    username,
                    lab.getTerm(), lab.getXq(), time,
                    com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                    lab.getZc(), lab.getZc(), false, 0, lab.getSrdd(), 0,
                    com.telephone.coursetable.Database.LAB.getFullLabName(lab.getCname(), lab.getItemname()) + "（备注：" + lab.getComm() + "）",
                    my_comment_map.get(new GoToClassKey(
                            username,
                            lab.getTerm(), lab.getXq(), time,
                            com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                            lab.getZc(), lab.getZc(), false
                    )), false
            ));
            classInfoDao.insert(new ClassInfo(
                    username,
                    com.telephone.coursetable.Database.LAB.getUniqueSerialNumber(lab.getXh(), lab.getBno() + ""),
                    "", "", "", "", "", lab.getSpname(), lab.getSpno(),
                    lab.getGrade(),
                    com.telephone.coursetable.Database.LAB.getLabName(lab.getCname()),
                    lab.getTeacherno(), lab.getName(), lab.getCourseid(),
                    lab.getPersons(), 0, 0, 0, 0, 0, lab.getStusct(), 0
            ));
        }
    }
}
