package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskBusinessTimeDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskEventDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskSysTimeDescribe;

public interface TimingTaskDao {
    List<TimeTaskDescribe> selectAllTimingTask();

    I18nDescribe selectTimingTaskI18N(@Param("international_id") String paramString);

    List<TimeTaskEventDescribe> selectTimeTaskEvent(@Param("task_id") String paramString);

    List<TimeTaskSysTimeDescribe> selectTimeTaskSysTime(@Param("task_id") String paramString);

    List<TimeTaskBusinessTimeDescribe> selectTimeTaskBusinessTime(@Param("task_id") String paramString);

    boolean deleteTimingTask(@Param("timeTask") TimeTaskDescribe paramTimeTaskDescribe);

    boolean insertTimingTask(@Param("timeTask") TimeTaskDescribe paramTimeTaskDescribe);

    boolean insertTimeTaskEvent(@Param("timeTask") TimeTaskEventDescribe paramTimeTaskEventDescribe);

    boolean insertTimeTaskSysTime(@Param("timeTask") TimeTaskSysTimeDescribe paramTimeTaskSysTimeDescribe);

    boolean insertTimeTaskBusinessTime(
            @Param("timeTask") TimeTaskBusinessTimeDescribe paramTimeTaskBusinessTimeDescribe);

    boolean insertInternational(@Param("i18n") I18nDescribe paramI18nDescribe);
}
