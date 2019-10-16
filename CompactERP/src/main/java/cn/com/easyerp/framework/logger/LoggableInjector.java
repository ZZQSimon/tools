package cn.com.easyerp.framework.logger;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LoggableInjector implements BeanPostProcessor {
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @SuppressWarnings("rawtypes")
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields((Class) bean.getClass(),
                (ReflectionUtils.FieldCallback) new ReflectionUtils.FieldCallback() {
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        ReflectionUtils.makeAccessible(field);
                        if (field.getAnnotation(Loggable.class) != null) {
                            final Logger log = LoggerFactory.getLogger((Class) bean.getClass());
                            field.set(bean, log);
                        }
                    }
                });
        return bean;
    }
}
