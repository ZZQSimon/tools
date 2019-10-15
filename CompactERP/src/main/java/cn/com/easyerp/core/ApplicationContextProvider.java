package cn.com.easyerp.core;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Autowired
    private ServletContext servletContext;

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public String getDaoPath() {
        return this.servletContext.getRealPath("/WEB-INF/classes/com/fujitsu/dx/core/dao");
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
}
