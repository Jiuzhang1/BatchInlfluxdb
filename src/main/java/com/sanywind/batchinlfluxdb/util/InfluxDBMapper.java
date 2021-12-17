package com.sanywind.batchinlfluxdb.util;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class InfluxDBMapper extends InfluxDBResultMapper {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.influx.database}")
    private String database;
    @Value("${spring.influx.retention-policy}")
    private String retentionPolicyName;
    @Value("${spring.influx.measurement}")
    private String measurement;
    @Value("${spring.influx.correct-windfarm}")
    private String correctwindFarm;
    @Value("${spring.influx.user}")
    private String user;
    @Value("${spring.influx.password}")
    private String password;
    @Value("${spring.influx.wrong-windfarm}")
    private String wrongwindFarm;
    @Resource
    private InfluxDB influxDB;
    @Resource
    private InfluxdbUtils influxdbUtils;
    @Autowired
    private MeasurementRetention measurementRetention;
    public void queryResult() throws ParseException {
        System.out.println("---开始查询数据---");

        Map measurements = new HashMap();
        measurements=measurementRetention.getMeasurements();
        measurements.forEach((key1,value1)-> {
            String retentionName=value1.toString();
            String measurementName=key1.toString();
            int count=1;
            int cursor=0;
            boolean stopFlag=true;
            //将当前时间转为influxdb能够认识的时间
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            //String currentTime = formatter.format(new Date());
            String stateDateString = "2021-06-11 12:40:00";
            String endDateString = "2021-03-21 14:00:00";
            //String stateDateString = "2021-09-24 12:41:46";
            //String endDateString = "2021-09-22 00:41:46";
            Date startDate=null;
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stateDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String currentTime = formatter.format(startDate);
            while (true){
                //查询条件
                String queryCondition = " WHERE time>'"+currentTime+"'-"+count+"d and time<='"+currentTime+"'-"+(count-1)+"d and farm='"+wrongwindFarm+"' TZ('Asia/Shanghai')";  //查询条件暂且为空
                String queryCmd = "SELECT * FROM "
                        +retentionName+"."+measurementName
                        + queryCondition;
                // 多个sql用分号隔开，因本次查询只有一个sql，所以取第一个就行
                System.out.println(queryCmd);
                List<Map> list = influxdbUtils.fetchRecords(queryCmd);
                //System.out.println(list);
                String tempDateString=formatter.format(getDateBefore(startDate,count-1));
                /*if (list!=null){
                    cursor++;
                }else{
                    if(cursor>0){
                        stopFlag=false;
                    }
                }*/
                if(tempDateString.compareTo(endDateString)<0){
                    stopFlag=false;
                }
                //System.out.println(cursor);
                System.out.println(stopFlag);
                if(!stopFlag) return;
                BatchPoints batchPoints = BatchPoints.database(database).retentionPolicy(retentionName)
                        .consistency(InfluxDB.ConsistencyLevel.ALL)
                        .build();
                if(list!=null){
                    for (int i = 0; i < list.size(); i++) {
                        Point.Builder builder = Point.measurement(measurementName);
                        Iterator iterator = list.get(i).entrySet().iterator();
                        String key =null;
                        String value =null;
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry)iterator.next();
                            key=entry.getKey().toString();
                            if("circle".equals(key)){
                                value=entry.getValue().toString();
                                if(value.equals("XVI")){
                                    value="16";
                                }else if(value.equals("XVII")){
                                    value="17";
                                }else if(value.equals("XVIII")){
                                    value="18";
                                }
                                //tag属性只能存储String类型
                                builder.tag(key, String.valueOf(value));
                            }else if("day".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("farm".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key,correctwindFarm );
                            }else if("month".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("project".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("turbine".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                //System.out.println(value);
                                builder.tag(key,value);
                            }else if("type".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("week".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("year".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("time".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  //yyyy-MM-dd'T'HH:mm:ss.SSSZ
                                Date date = null;
                                try {
                                    date = df.parse(value);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String df3=df2.format(date);
                                Number  a = null;
                                try {
                                    a = df2.parse(String.valueOf(df3)).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                builder.time(a, TimeUnit.MILLISECONDS);
                            }else if("availabilitySta".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("availabilityStaDesc".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("brakeProNo".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("faultLevel".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("first".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("part".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("statusCode".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else if("statusDesc".equals(key)){
                                value=entry.getValue().toString();
                                //设置field
                                builder.tag(key, String.valueOf(value));
                            }else{
                                if(entry.getValue()!=null){
                                    value=entry.getValue().toString();
                                    if("MC131".equals(key)){
                                        builder.addField(key,Double.valueOf(value));
                                    }else if("endTime".equals(key)){
                                        Object number=entry.getValue();
                                        double douNumber=Double.parseDouble(number.toString());
                                        builder.addField(key,Double.valueOf(douNumber));
                                    }else{
                                        //判断value是否为数字
                                        if(isNumeric(value)){
                                            Object number=entry.getValue();
                                            double douNumber=Double.parseDouble(number.toString());
                                            builder.addField(key,Double.valueOf(douNumber));
                                            //builder.addField(key,Double.valueOf(value));
                                        }else{
                                            builder.addField(key,String.valueOf(value));
                                        }
                                    }
                                }else{
                                    //value="0";
                                    //builder.addField(key,value);
                                }

                            }
                            builder.tag("history", "history");
                        }
                        batchPoints.point(builder.build());
                    }
                    influxDB.write(batchPoints);
                    System.out.println(measurementName);
                }
                count+=1;
            }
        });

    }
    public static boolean isNumeric(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        String regx = "[+-]*\\d+\\.?\\d*[Ee]*[+-]*\\d+";
        Pattern pattern = Pattern.compile(regx);
        boolean isNumber = pattern.matcher(str).matches();
        if (isNumber) {
            return isNumber;
        }
        regx = "^[-\\+]?[.\\d]*$";
        pattern = Pattern.compile(regx);
        return pattern.matcher(str).matches();
    }
    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }
}
