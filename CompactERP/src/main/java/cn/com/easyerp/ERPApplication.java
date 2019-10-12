package cn.com.easyerp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
// @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableAsync
public class ERPApplication extends SpringBootServletInitializer {
    /**
     * IDEA运行时 运行此函数
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ERPApplication.class, args);
    }

    /**
     * @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
     *      导出war在外部容器使用时, 不能使用main函数运行, 需要配置此项
     * 
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(this.getClass());
        return super.configure(application);
    }
}
