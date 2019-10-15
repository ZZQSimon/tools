package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.map.AttendanceArea;

@Repository
public interface AttendanceDao {
    List<String> selectAttendanceAreaByArea(@Param("attendanceArea") String paramString);

    void saveAttendanceArea(@Param("id") String paramString1, @Param("name") String paramString2,
            @Param("area") String paramString3, @Param("lon") String paramString4, @Param("lat") String paramString5,
            @Param("user") String paramString6, @Param("date") String paramString7);

    void updateAttendanceTime(@Param("begin") String paramString1, @Param("end") String paramString2,
            @Param("id") String paramString3);

    List<AttendanceArea> selectAttendanceArea();

    void removeAttendanceArea(@Param("ids") List<String> paramList);
}
