package cn.com.easyerp.core.schedule;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import cn.com.easyerp.core.logger.Loggable;

public class DxTaskScheduler
  extends ConcurrentTaskScheduler
{
  @Loggable
  private Logger logger;
  @Autowired
  private TransactionTemplate txTemplate;
  private ScheduledExecutorService executor;
  
  private static class DxTask
    implements Runnable
  {
    private Runnable runnable;
    private String domain;
    private TransactionTemplate txTemplate;
    
    DxTask(Runnable runnable, String domain, TransactionTemplate txTemplate) {
      this.runnable = runnable;
      this.domain = domain;
      this.txTemplate = txTemplate;
    }


    
    public void run() {
      dxDomain.set(this.domain);
      
      try { if (this.txTemplate != null) {
          this.txTemplate.execute(new TransactionCallback()
              {
                
                public Object doInTransaction(TransactionStatus status)
                {
                  DxTaskScheduler.DxTask.this.runnable.run();
                  return null;
                }
              });
        } else {
          this.runnable.run();
        }  }
      finally { dxDomain.remove(); }
    
    }
  }
  
  private static ThreadLocal<String> dxDomain = new ThreadLocal<String>()
    {
      protected String initialValue()
      {
        return null;
      }
    };


  
  public static String getDomain() { return (String)dxDomain.get(); }



  
  public DxTaskScheduler() {
    this.executor = Executors.newScheduledThreadPool(100);
    setConcurrentExecutor(this.executor);
    setScheduledExecutor(this.executor);
  }


  
  public void destroy() { this.executor.shutdownNow(); }



  
  public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
    task = decorateTask(task);
    return super.schedule(task, trigger);
  }


  
  public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
    task = decorateTask(task);
    return super.schedule(task, startTime);
  }


  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
    task = decorateTask(task);
    return super.scheduleAtFixedRate(task, startTime, period);
  }


  
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
    task = decorateTask(task);
    return super.scheduleAtFixedRate(task, period);
  }


  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
    task = decorateTask(task);
    return super.scheduleWithFixedDelay(task, startTime, delay);
  }


  
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
    task = decorateTask(task);
    return super.scheduleWithFixedDelay(task, delay);
  }

  
  private Runnable decorateTask(Runnable task) {
    ScheduledMethodRunnable runnable = (ScheduledMethodRunnable)task;
    Method m = runnable.getMethod();
    Object target = runnable.getTarget();
    if (AopUtils.isJdkDynamicProxy(target)) {
      Class<?> clazz = AopUtils.getTargetClass(target);
      try {
        m = clazz.getMethod(m.getName(), m.getParameterTypes());
      } catch (NoSuchMethodException e) {
        this.logger.error("failed build task :" + clazz.getName() + "#" + m.getName());
        return new DxTask(task, null, this.txTemplate);
      } 
    } 
    if (!m.isAnnotationPresent(DxTaskDomain.class)) {
      return new DxTask(task, null, this.txTemplate);
    }
    DxTaskDomain annotation = (DxTaskDomain)m.getAnnotation(DxTaskDomain.class);
    String domain = annotation.value();
    return new DxTask(task, domain, annotation.tx() ? this.txTemplate : null);
  }
}
