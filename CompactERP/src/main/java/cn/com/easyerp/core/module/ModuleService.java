package cn.com.easyerp.core.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.framework.enums.LogLevel;

@Service
public class ModuleService {

    private static final HashMap<LogLevel, Level> levelMapping = new HashMap<>(LogLevel.values().length);
    static {
        levelMapping.put(LogLevel.ALL, Level.ALL);
        levelMapping.put(LogLevel.DEBUG, Level.DEBUG);
        levelMapping.put(LogLevel.ERROR, Level.ERROR);
        levelMapping.put(LogLevel.FATAL, Level.FATAL);
        levelMapping.put(LogLevel.INFO, Level.INFO);
        levelMapping.put(LogLevel.OFF, Level.OFF);
        levelMapping.put(LogLevel.TRACE, Level.TRACE);
        levelMapping.put(LogLevel.WARN, Level.WARN);
    }

    @Autowired
    private ApplicationContextProvider acp;
    @Autowired(required = false)
    private ModuleConfigureAdapter adapter;
    private ModuleDescribe[] modules;
    private Set<String> js = new LinkedHashSet<>();
    private Set<String> css = new LinkedHashSet<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostConstruct
    public void init() {
        if (this.adapter != null) {
            ModuleConfig config = new ModuleConfig();
            this.adapter.configure(config);
            this.js.addAll(config.js);
            this.css.addAll(config.css);
            configLog(config.log);
        }

        Collection<Object> beans = this.acp.getApplicationContext().getBeansWithAnnotation(Module.class).values();
        this.modules = new ModuleDescribe[beans.size()];
        int i = 0;
        for (Object bean : beans) {
            Class clazz = bean.getClass();

            if (Enhancer.isEnhanced(clazz))
                clazz = clazz.getSuperclass();
            Module module = (Module) clazz.getAnnotation(Module.class);
            ModuleDescribe m = this.modules[i++] = new ModuleDescribe(module);
            this.js.addAll(m.getJs());
            this.css.addAll(m.getCss());
        }
    }

    private void configLog(LogConfig config) {
        if (config.logLevel != null) {
            Logger logger4j = Logger.getRootLogger();
            logger4j.setLevel((Level) levelMapping.get(config.logLevel));
        }
    }

    public Set<String> getModuleJs() {
        return this.js;
    }

    public Set<String> getModuleCss() {
        return this.css;
    }
}
