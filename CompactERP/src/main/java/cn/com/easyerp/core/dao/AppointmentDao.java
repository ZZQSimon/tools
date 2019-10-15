package cn.com.easyerp.core.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.companyCalendar.appointment.AppointmentDescribe;

@Repository
public interface AppointmentDao {
    Map<String, Object> selectAppointmentData(@Param("table") String paramString,
            @Param("idColumn") Map<String, Object> paramMap);

    AppointmentDescribe selectAppointmentById(@Param("id") String paramString);

    int updateAppointment(@Param("data") Map<String, Object> paramMap,
            @Param("appointment") AppointmentDescribe paramAppointmentDescribe);

    int createAppointment(@Param("data") Map<String, Object> paramMap, @Param("table") String paramString);
}
