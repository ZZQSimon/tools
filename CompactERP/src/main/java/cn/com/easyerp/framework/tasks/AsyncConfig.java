/**
	* Title: AsyncConfig.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2018.12.14 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/
package cn.com.easyerp.framework.tasks;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @ClassName: AsyncConfig <br>
 * @Description: [] <br>
 * @date 2018.12.14 <br>
 * 
 * @author Simon Zhang
 */

@Configuration
@EnableAsync
public class AsyncConfig {
    public static int corePoolSize = 10;
    public static int maxPoolSize = 200;
    public static int queueCapacity = 10;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        // executor.setCorePoolSize(Attribute.corePoolSize);
        // executor.setMaxPoolSize(Attribute.maxPoolSize);
        // executor.setQueueCapacity(Attribute.queueCapacity);
        executor.initialize();
        return executor;
    }
}
