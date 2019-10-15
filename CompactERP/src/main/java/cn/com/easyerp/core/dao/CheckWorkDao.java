package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.companyCalendar.AttendanceInfo;
import cn.com.easyerp.core.companyCalendar.CheckWorkJson;

@Repository
public interface CheckWorkDao {
    List<CheckWorkJson> selectAttendanceCount(@Param("date") String paramString1, @Param("name") String paramString2);

    List<String> selectAttendanceCountUser(@Param("date") String paramString1, @Param("name") String paramString2,
            @Param("offset") Integer paramInteger1, @Param("size") Integer paramInteger2);

    List<AttendanceInfo> selectAttendances(@Param("user_id") String paramString1,
            @Param("beginDate") String paramString2, @Param("endDate") String paramString3);

    List<String> selectAttendanceCountUserCount(@Param("date") String paramString1, @Param("name") String paramString2);
}
