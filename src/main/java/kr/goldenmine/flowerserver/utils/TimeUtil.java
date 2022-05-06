package kr.goldenmine.flowerserver.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeUtil {

    public static String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println(timestamp); // 생성한 timestamp 출력

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        System.out.println(sdf.format(timestamp)); // format을 사용해 출력

        return sdf.format(timestamp);
    }
}
