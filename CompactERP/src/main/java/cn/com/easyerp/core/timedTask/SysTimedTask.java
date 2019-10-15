package cn.com.easyerp.core.timedTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysTimedTask {
    @Autowired
    private SysTimedTaskService sysTimedTaskService;

    @Transactional
    @Scheduled(cron = "10 * * * * *")
    public void sysTask() {
        this.sysTimedTaskService.execTask();
    }
}
